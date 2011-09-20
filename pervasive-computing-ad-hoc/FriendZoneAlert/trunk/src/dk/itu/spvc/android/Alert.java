package dk.itu.spvc.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import dk.itu.infobus.ws.EventBuilder;
import dk.itu.infobus.ws.EventBus;
import dk.itu.infobus.ws.Generator;
import dk.itu.infobus.ws.GeneratorAdapter;
import dk.itu.infobus.ws.Listener;
import dk.itu.infobus.ws.PatternBuilder;
import dk.itu.infobus.ws.PatternOperator;

public class Alert extends Activity implements OnClickListener {
	String userName, reqName;
	double minLat, maxLat, minLon, maxLon;
	EventBus eb;
	Generator gen;
	Listener lis;
	LocationListener locLis;
	String TAG = "Activiy.Alert";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent startIntent = getIntent();
		userName = startIntent.getExtras().getString("user.username");
		reqName = startIntent.getExtras().getString("user.requsrname");
		minLat = startIntent.getDoubleExtra("lat.min", 0.0);
		maxLat = startIntent.getDoubleExtra("max.lat", 0.0);
		minLon = startIntent.getDoubleExtra("lon.min", 0.0);
		maxLon = startIntent.getDoubleExtra("lon.max", 0.0);
		
		setContentView(R.layout.alert);
		
		((Button)findViewById(R.id.btnGo)).setOnClickListener(this);
		
		locLis = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
			     gen.publish(new EventBuilder()
		            .put("user", userName)
		            .put("latitude", location.getLatitude())
		            .put("longitude", location.getLongitude())
		            .put("accuracy", location.getAccuracy())
		            .getEvent());
			}
		};
		
		eb = new dk.itu.infobus.ws.EventBus("tiger.itu.dk",8004);
		
		try {
			eb.start();
			gen = new GeneratorAdapter(userName+"LocationGenerator", "user", "latitude", "longitude","accuracy");
			eb.addGenerator(gen);
			lis = new Listener(new PatternBuilder()
			.add("user", PatternOperator.EQ, reqName)
			.add("latitude", PatternOperator.GT, minLat)
			.add("latitude", PatternOperator.LT, maxLat)
			.add("longitude", PatternOperator.GT, minLon)
			.add("longitude", PatternOperator.LT, maxLon)
			.addMatchAll("accuracy")
			.getPattern()) {
				@Override
				public void cleanUp() throws Exception {
					android.util.Log.i(TAG, "stopping listener");
				}
				
				@Override
				public void onMessage(java.util.Map<String,Object> msg) {
					setAlertText((Double)msg.get("latitude"), (Double)msg.get("longitude"));
				}
				
				@Override
				public void onStarted() {
					Log.i(TAG, "listener started");
					
				}
			};
			eb.addListener(lis);
			
			LocationManager locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = locMan.getProviders(true);
			if (providers.size() > 0) {
				locMan.requestLocationUpdates(providers.get(0), 1000, 1, locLis);
				
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAlertText(final double lat, final double lon) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) findViewById(R.id.FriendInZoneAlert))
						.setText("User " + reqName + " in zone! lat: " + lat
								+ ", lon: " + lon);

			}
		});
	}

	@Override
	public void onClick(View v) {
		// get the location manager service and remove the updates for
		// locListener
		((LocationManager) this.getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(locLis);
		// stop the eventbus. Ignore exceptions
		try {
			eb.stop();
		} catch (Exception ignored) {
		}
		// start the SetUp activity
		this.startActivity(new Intent(this, Setup.class));
	}
	
}
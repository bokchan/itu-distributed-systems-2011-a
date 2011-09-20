package dk.itu.spvc.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button b = (Button) findViewById(R.id.btnGo);
        b.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		String usrname = getTxt(R.id.username);
		String reqUsrname = getTxt(R.id.reqUsername);
		Double minLat = Double.parseDouble( getTxt(R.id.minLat));
		Double maxLat = Double.parseDouble( getTxt(R.id.maxLat ));
		Double minLon = Double.parseDouble( getTxt(R.id.minLon));
		Double maxLon = Double.parseDouble( getTxt(R.id.maxLon) );
		
		Intent intent = new Intent(this, Alert.class);
		
		intent.putExtra("user.username", usrname)
		.putExtra("user.requsrname", reqUsrname)
		.putExtra("lat.min", minLat)
		.putExtra("lat.max", maxLat)
		.putExtra("lon.min", minLon)
		.putExtra("lon.max", maxLon);
		
		this.startActivity(intent);
	}
	
	private String getTxt(int id) { 
		return ((EditText) findViewById(id)).getText().toString();
	}
}
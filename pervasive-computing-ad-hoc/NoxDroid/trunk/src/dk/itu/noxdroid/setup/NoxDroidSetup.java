package dk.itu.noxdroid.setup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;
import dk.itu.noxdroid.R;


public class NoxDroidSetup extends Activity {
	ViewFlipper flipper;
	Button btnSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		btnSwitch = (Button) findViewById(R.id.btnSwitch);
		flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		flipper.setInAnimation(this, android.R.anim.fade_in);
		flipper.setOutAnimation(this, android.R.anim.fade_out);
	}
	
	public void clickHandler(View view) {
		//flipper.showNext();
		flipper.setFlipInterval(1000);
		flipper.startFlipping();
	}

}

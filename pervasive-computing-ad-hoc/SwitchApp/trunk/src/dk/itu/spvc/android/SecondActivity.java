package dk.itu.spvc.android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity implements OnClickListener {
	
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.second);
		Button gotoSecond = (Button) findViewById(R.id.GotoFirstButton);
		gotoSecond.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,FirstActivity.class);
		startActivity(intent);
	}

}

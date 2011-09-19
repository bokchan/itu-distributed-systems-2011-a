package dk.pellekrogholt.labclass4app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class SecondActivity extends Activity implements OnClickListener {
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second); 
        // similar to what we did in first activity setContentView(R.layout.main); 
        
        final Button button = (Button) findViewById(R.id.GoToFirstButton);
        button.setOnClickListener(this);
        
    }

    /* Go to the first activity*/
	@Override
	public void onClick(View v) {
	    Intent intent = new Intent(this, FirstActivity.class);
	    startActivity(intent);		
	}    

	
	
	
	

}
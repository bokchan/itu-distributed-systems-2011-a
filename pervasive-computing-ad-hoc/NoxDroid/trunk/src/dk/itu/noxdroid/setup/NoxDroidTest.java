package dk.itu.noxdroid.setup;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.os.Bundle;
import android.util.Log;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.service.NoxDroidService;

public class NoxDroidTest extends AbstractIOIOActivity {
	
	private NoxDroidService service;
	private String TAG = "IOIOSensorActivity";  
	private boolean flag = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ioio);
	}

	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private AnalogInput input_;
		private DigitalOutput led_;
		private int pinLed = 9;
		private int pinIn = 11;

		@Override
		public void setup() throws ConnectionLostException {
			addToDebug("Setup()");
			try {
				// input_ = ioio_.openAnalogInput(pinIn);
				led_ = ioio_.openDigitalOutput(pinLed, Spec.Mode.NORMAL, true);
			} catch (ConnectionLostException e) {
				throw e;
			}
		}

		@Override
		public void loop() throws ConnectionLostException {
			addToDebug("Loop");
			try {
//				final float reading = input_.read();
//				setText(Float.toString(reading));
				led_.write(!flag);
				sleep(1000);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				throw e;
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		addToDebug("Return new IOIOThread");
		return new IOIOThread();
	}

	private void addToDebug(final String str) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, str);
			};
		});
		
	}

}

package dk.itu.noxdroid.ioio;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.exception.IncompatibilityException;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import dk.itu.noxdroid.service.NoxDroidService;

public class NoxDroidIOIOThread extends Thread  {
	Context context;
	
	private java.util.Observable publisher;

	private String TAG = "NoxDroidIOIOThread";
	/** Subclasses should use this field for controlling the IOIO. */
	protected IOIO ioio_;
	private boolean flag = false;
	private boolean abort_ = false;
	private boolean connected_ = true;

	private AnalogInput input_;

	private DigitalOutput led_;
	private int pinLed = 16;
	private int pinIn = 40;
	private Map<String, ?> app_prefs;
	NoxDroidService service;

	public NoxDroidIOIOThread(NoxDroidService service) {
		this.service = service;
		publisher = new NoxDroidObservable();
		
	}
	
	class NoxDroidObservable extends java.util.Observable {
		
	}

	/** Not relevant to subclasses. */
	@Override
	public final void run() {
		super.run();
		while (true) {
			try {
				synchronized (this) {
					if (abort_) {
						break;
					}
					ioio_ = IOIOFactory.create();
				}
				ioio_.waitForConnect();
				connected_ = true;
				setup();
				while (!abort_) {
					loop();
				}
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				if (abort_) {
					break;
				}
			} catch (InterruptedException e) {
				ioio_.disconnect();
				break;
			} catch (IncompatibilityException e) {
				Log.e("AbstractIOIOActivity", "Incompatible IOIO firmware", e);
				incompatible();
				// nothing to do - just wait until physical disconnection
				try {
					ioio_.waitForDisconnect();
				} catch (InterruptedException e1) {
					ioio_.disconnect();
				}
			} catch (Exception e) {
				Log.e("AbstractIOIOActivity", "Unexpected exception caught", e);
				ioio_.disconnect();
				break;
			} finally {
				try {
					if (ioio_ != null) {
						ioio_.waitForDisconnect();
						if (connected_) {
							disconnected();
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Subclasses should override this method for performing operations to be
	 * done once as soon as IOIO communication is established. Typically, this
	 * will include opening pins and modules using the openXXX() methods of the
	 * {@link #ioio_} field.
	 */
	protected void setup() throws ConnectionLostException, InterruptedException {
		addToDebug("Setup()");
		try {
			input_ = ioio_.openAnalogInput(pinIn);
			led_ = ioio_.openDigitalOutput(pinLed, Spec.Mode.NORMAL, true);

		} catch (ConnectionLostException e) {
			throw e;
		}
	}

	/**
	 * Subclasses should override this method for performing operations to be
	 * done repetitively as long as IOIO communication persists. Typically, this
	 * will be the main logic of the application, processing inputs and
	 * producing outputs.
	 */
	protected void loop() throws ConnectionLostException, InterruptedException {

		addToDebug("Loop");
		try {
			final float reading = input_.read();
			//addToDebug(Float.toString(reading));
			led_.write(!flag);
			flag = flag ? false : true;
			Object obj = (Object) reading ;
			service.update(this.getClass(), obj);
			sleep(1000);
		} catch (InterruptedException e) {
			ioio_.disconnect();
		} catch (ConnectionLostException e) {
			throw e;
		}
	}

	/**
	 * Subclasses should override this method for performing operations to be
	 * done once as soon as IOIO communication is lost or closed. Typically,
	 * this will include GUI changes corresponding to the change. This method
	 * will only be called if setup() has been called. The {@link #ioio_} member
	 * must not be used from within this method.
	 */
	protected void disconnected() throws InterruptedException {

	}

	/**
	 * Subclasses should override this method for performing operations to be
	 * done if an incompatible IOIO firmware is detected. The {@link #ioio_}
	 * member must not be used from within this method. This method will only be
	 * called once, until a compatible IOIO is connected (i.e. {@link #setup()}
	 * gets called).
	 */
	protected void incompatible() {
	}

	/** Not relevant to subclasses. */
	public synchronized final void abort() {
		abort_ = true;
		if (ioio_ != null) {
			ioio_.disconnect();
		}
		if (connected_) {
			interrupt();
		}
	}

	public synchronized Float getReading() throws ConnectionLostException {
		if (input_ != null) {
			try {
				return input_.read();
			} catch (InterruptedException e) {
				// ioio_.disconnect();
			}
		}
		return null;
	}

	private void addToDebug(final String str) {
		Log.i(TAG, str);
	}
}
package dk.itu.noxdroid.ioio;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.exception.IncompatibilityException;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import dk.itu.noxdroid.R;
import dk.itu.noxdroid.database.DbAdapter;
import dk.itu.noxdroid.service.NoxDroidService;
import dk.itu.noxdroid.util.SensorDataUtil;

public class NoxDroidIOIOThread extends Thread {
	private String TAG;
	/** Subclasses should use this field for controlling the IOIO. */
	protected IOIO ioio_;
	private boolean flag = false;
	private boolean abort_ = false;
	private boolean connected_ = false;

	private AnalogInput input_;

	private DigitalOutput ledGreen_;
	private DigitalOutput ledYellow_;
	private DigitalOutput ledRed_;
	private int pinGreen = 16;
	private int pinYellow = 18;
	private int pinledRed = 20;
	private int pinAnalogIn = 40;
	
	private DbAdapter mDbHelper;
	private double nox;
	private double temperature;
	
	ArrayList<IOIOEventListener> listeners = new ArrayList<IOIOEventListener>();
	private NoxDroidService service;

	public NoxDroidIOIOThread(NoxDroidService service) {
		this.service = service;
		listeners.add(service);
		TAG = service.getString(R.string.LOGCAT_TAG, service
				.getString(R.string.app_name), this.getClass().getSimpleName());

		// pinGreen = (Integer)
		// service.APP_PREFS.get(dk.itu.noxdroid.R.string.IOIO_LED_GREEN_PIN);
		// pinYellow = (Integer)
		// service.APP_PREFS.get(dk.itu.noxdroid.R.string.IOIO_LED_YELLOW_PIN);
		// pinledRed = (Integer)
		// service.APP_PREFS.get(dk.itu.noxdroid.R.string.IOIO_LED_RED_PIN);
		// pinAnalogIn = (Integer) service.APP_PREFS.get(R.string.IOIO_NO2_PIN);
	}

	/** Not relevant to subclasses. */
	@Override
	public final void run() {
		super.run();
		while (true) {
			try {
				synchronized (this) {
					if (abort_) {
						notifyEventchanged(NoxDroidService.ERROR_IOIO_ABORTED);
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
				Log.e(TAG, e.getMessage());
				if (abort_) {
					notifyEventchanged(NoxDroidService.ERROR_IOIO_CONNECTION_LOST);
					break;
				}
			} catch (InterruptedException e) {
				notifyEventchanged(NoxDroidService.ERROR_IOIO_INTERRUPTED);
				Log.e(TAG, e.getMessage());
				ioio_.disconnect();
				break;
			} catch (IncompatibilityException e) {
				Log.e("AbstractIOIOActivity", "Incompatible IOIO firmware", e);
				notifyEventchanged(NoxDroidService.ERROR_IOIO_INCOMPATIBLE);
				incompatible();
				// nothing to do - just wait until physical disconnection
				try {
					ioio_.waitForDisconnect();
				} catch (InterruptedException e1) {
					
					Log.e(TAG, e.getMessage());
					ioio_.disconnect();
				}
			} catch (Exception e) {
				Log.e("AbstractIOIOActivity", "Unexpected exception caught", e);
				notifyEventchanged(NoxDroidService.ERROR_IOIO_CONNECTION_LOST);
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
					Log.e(TAG, e.getMessage());
					notifyEventchanged(NoxDroidService.ERROR_IOIO_INTERRUPTED);
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
		try {
			input_ = ioio_.openAnalogInput(pinAnalogIn);
			ledGreen_ = ioio_.openDigitalOutput(pinGreen, Spec.Mode.NORMAL,
					true);
			ledYellow_ = ioio_.openDigitalOutput(pinYellow, Spec.Mode.NORMAL,
					true);
			ledRed_ = ioio_
					.openDigitalOutput(pinledRed, Spec.Mode.NORMAL, true);

			/*
			 * Set up data base
			 * TODO: Not 100% sure about if service can be used as context ? 
			 */
	        mDbHelper = new DbAdapter(service);
	        mDbHelper.open();
	        
			
		} catch (ConnectionLostException e) {

			
			/* 
			 * Close database - also done in other exceptions
			 * TODO: verify when it should be closed
			 */
			mDbHelper.close();

			Log.e(TAG, e.getMessage());
			notifyEventchanged(NoxDroidService.ERROR_IOIO_CONNECTION_LOST);

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
		try {
			final float reading = SensorDataUtil.muAtoMuGrames(input_.read());
			// addToDebug(Float.toString(reading));
			ledGreen_.write(!flag);
			ledYellow_.write(!flag);
			ledRed_.write(flag);
			flag = flag ? false : true;
			
			// TODO: probably just disable this part
			// because we are not going to send data directly back to the UI
			Object obj = (Object) reading;
			
			service.update(this.getClass(), obj);
			
			sleep(1000);
			
			// TODO: add right values
	    	nox = 10101.0;
	    	temperature = 38.8;
			//mDbHelper.createNox(nox, temperature);
			Log.i(TAG, "calling mDbHelper.createNox(nox, temperature) - should add row to the nox table in noxdroid.db");
			

		} catch (InterruptedException e) {
			notifyEventchanged(NoxDroidService.ERROR_IOIO_INTERRUPTED);
			Log.i(TAG, e.getMessage());
			ioio_.disconnect();
		} catch (ConnectionLostException e) {
			notifyEventchanged(NoxDroidService.ERROR_IOIO_CONNECTION_LOST);
			// Notify service;
			Log.e(TAG, e.getMessage());
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
		/* 
		 * Close database - also done in other exceptions
		 * TODO: verify when it should be closed
		 */
//        mDbHelper.close();
		// Temporarily disabled it crashed application - probably not the right place to stop or
		
		
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
		notifyEventchanged(NoxDroidService.ERROR_IOIO_ABORTED);
	}

	public synchronized Float getReading() throws ConnectionLostException {
		if (input_ != null) {
			try {
				return input_.read();
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
				abort();
			}
		}
		return null;
	}

	private void notifyEventchanged(int msg) {
		Iterator<IOIOEventListener> it = listeners.iterator();
		while (it.hasNext()) {
			it.next().notify(msg);
		}
	}
	
	public boolean isConnected() {
		return this.connected_;
	}
}
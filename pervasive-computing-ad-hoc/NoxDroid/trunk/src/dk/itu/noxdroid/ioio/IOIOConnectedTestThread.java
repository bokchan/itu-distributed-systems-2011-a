package dk.itu.noxdroid.ioio;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.DigitalOutput.Spec;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.exception.IncompatibilityException;
import android.util.Log;

public class IOIOConnectedTestThread extends Thread {

	private IOIO ioio_;
	private DigitalOutput ledGreen_;
	private DigitalOutput ledYellow_;
	private DigitalOutput ledRed_;
	private int pinGreen = 16;
	private int pinYellow = 18;
	private int pinRed = 20;
	private int pinAnalogIn = 40;

	private AnalogInput input_;

	private DigitalOutput led_;
	private int pinLed = 9;
	private int pinIn = 40;

	private boolean flag = false;
	private boolean abort_ = false;
	private boolean connected_ = true;

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
				abort();

			} catch (ConnectionLostException e) {
				if (abort_) {
					abort();
					break;
				}
			} catch (IncompatibilityException e) {
				Log.e("AbstractIOIOActivity", "Incompatible IOIO firmware", e);
				incompatible();
				// nothing to do - just wait until physical disconnection
				try {
					ioio_.waitForDisconnect();
					break;
				} catch (InterruptedException e1) {
					ioio_.disconnect();
					break;
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

	public boolean setup() throws ConnectionLostException {
		try {
			input_ = ioio_.openAnalogInput(pinIn);
			led_ = ioio_.openDigitalOutput(pinLed, Spec.Mode.NORMAL, true);
			abort();
			return true;

		} catch (ConnectionLostException e) {
			return false;

		}
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
}
package dk.itu.smds.android.bluetweet;

import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import dk.itu.android.bluetooth.BluetoothAdapter;
import dk.itu.android.bluetooth.BluetoothClass;
import dk.itu.android.bluetooth.BluetoothDevice;

public class BluetoothDeviceObtainer {
	static final String TAG = BluetoothDeviceObtainer.class.getSimpleName();
	
	BluetoothAdapter adapter;
	String localAddress;
	public BluetoothDeviceObtainer(BluetoothAdapter adapter) {
		this.adapter = adapter;
		this.localAddress = adapter.getAddress();
	}

	/**
	 * Add the maybeAdd device if:
	 * <ul>
	 * <li>its address compares less than the local address</li>
	 * <li>it is a computer or a phone</li>
	 * </ul>
	 * @param maybeAdd
	 * @param devices
	 */
	private void filter( BluetoothDevice maybeAdd, Set<BluetoothDevice> devices ) {
		int cls = maybeAdd.getBluetoothClass().getMajorDeviceClass();
		if(localAddress.compareTo(maybeAdd.getAddress()) > 0 &&
			(cls == BluetoothClass.Device.Major.COMPUTER || cls == BluetoothClass.Device.Major.PHONE)) {
			Log.i(TAG,"\t Adding pc/phone device - "+maybeAdd.getAddress());
			devices.add( maybeAdd );
		}
	}
	
	Set<BluetoothDevice> lookupDevices(Context ctxt, boolean discover) {
		final Set<BluetoothDevice> out = new HashSet<BluetoothDevice>();
		/*
		 * perform discovery, if required
		 * add paired devices
		 * filter only computers and cellphones
		 */
		if(discover) {
			// create an object that we will lock on while discovering
			final Object lock = new Object();
			// create the broadcast receiver
			final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					// if we fond a device, try to add it to the set of devices
					if(BluetoothDevice.ACTION_FOUND.equals(action)) {
						BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						Log.i(TAG,"found device: " + device.getAddress());
						filter(device, out);
					// if the end of the discovery phase has been reached, notify on the lock object
					} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
						synchronized(lock) {
							lock.notify();
						}
					}
				}
			};
			
			// register the receiver for FOUND and DISCOVERY_FINISHED actions
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			ctxt.registerReceiver(mReceiver, filter);
			filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			ctxt.registerReceiver(mReceiver, filter);
			
			// cancel the current (if any) discovery start the discovery phase
			adapter.cancelDiscovery();
			adapter.startDiscovery();
			
			// wait until the discovery is finished
			synchronized(lock) {
				try {
					lock.wait();
				} catch(InterruptedException e){
					Log.w(TAG, "interrupted while receiving bt devices!", e);
				}
			}
			
			// unregister the broadcast receiver
			ctxt.unregisterReceiver(mReceiver);
		}

		// filter the bonded devices set
		for(BluetoothDevice d : adapter.getBondedDevices()) {
			filter(d,out);
		}

		return out;
	}
}

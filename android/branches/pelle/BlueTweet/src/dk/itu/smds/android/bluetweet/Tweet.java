package dk.itu.smds.android.bluetweet;

public class Tweet {
	
	String bluetoothAddress;
	String deviceName;
	String message;
	long timestamp;
	
	public Tweet() {
	}
	public Tweet(String bluetoothAddress, String deviceName, String message,
			long timestamp) {
		this.bluetoothAddress = bluetoothAddress;
		this.deviceName = deviceName;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public String getBluetoothAddress() {
		return bluetoothAddress;
	}
	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}

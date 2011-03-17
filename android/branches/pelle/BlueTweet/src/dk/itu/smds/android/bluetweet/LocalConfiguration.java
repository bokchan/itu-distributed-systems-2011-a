package dk.itu.smds.android.bluetweet;

public class LocalConfiguration {

	String localBluetoothAddress;
	long interval;
	long scanDuration;
	long ageTime;
	
	public LocalConfiguration(String localBluetoothAddress, long interval,
			long scanDuration, long ageTime) {
		this.localBluetoothAddress = localBluetoothAddress;
		this.interval = interval;
		this.scanDuration = scanDuration;
		this.ageTime = ageTime;
	}

	public String getLocalBluetoothAddress() {
		return localBluetoothAddress;
	}

	public long getInterval() {
		return interval;
	}

	public long getScanDuration() {
		return scanDuration;
	}

	public long getAgeTime() {
		return ageTime;
	}
}

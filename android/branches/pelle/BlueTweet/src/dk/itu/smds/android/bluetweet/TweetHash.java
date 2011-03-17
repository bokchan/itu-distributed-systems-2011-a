package dk.itu.smds.android.bluetweet;

public class TweetHash implements Comparable<TweetHash> {

	String originAddress;
	long timestamp;
	
	public TweetHash(String originAddress, long timestamp) {
		this.originAddress = originAddress;
		this.timestamp = timestamp;
	}
	
	public String getOriginAddress() {
		return originAddress;
	}
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TweetHash))
			return false;
		TweetHash other = (TweetHash)o;
		return other.originAddress.equals(originAddress) && other.timestamp == timestamp;
	}
	
	public int compareTo(TweetHash another) {
		return originAddress.compareTo(another.originAddress) + (int)(timestamp-another.timestamp);
	}
	
}

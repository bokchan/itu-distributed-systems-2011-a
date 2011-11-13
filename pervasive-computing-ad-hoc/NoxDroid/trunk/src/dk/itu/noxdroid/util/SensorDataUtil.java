package dk.itu.noxdroid.util;

public class SensorDataUtil {
//	Sensor output
//	2.2 ± 0.5 µA/ppm
	
	/**
	 * 
	 * @param a the output in muA received from the sensor 
	 * @return the sensor data point converted to mu grames per sq. meter
	 */
	public static float muAtoMuGrames(float a) {
		return (float ) 1880.0f * a / 2.2f;
	}
}

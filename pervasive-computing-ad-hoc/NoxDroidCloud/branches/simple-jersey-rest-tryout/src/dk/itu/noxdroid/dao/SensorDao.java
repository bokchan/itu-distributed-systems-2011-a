package dk.itu.noxdroid.dao;

import java.util.HashMap;
import java.util.Map;

import dk.itu.noxdroid.model.Sensor;


public enum SensorDao {
	instance;
	
	private Map<String, Sensor> contentProvider = new HashMap<String, Sensor>();
	
	private SensorDao() {
		
		Sensor sensor = new Sensor("1", "Sensor 1");
		sensor.setDescription("Sensor 1 description");
		contentProvider.put("1", sensor);
		sensor = new Sensor("2", "Sensor 2");
		sensor.setDescription("Sensor 2 description");
		contentProvider.put("2", sensor);
		
	}
	public Map<String, Sensor> getModel(){
		return contentProvider;
	}
	
}
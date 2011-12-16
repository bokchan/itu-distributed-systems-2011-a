package dk.itu.noxdroidcloudengine.dataprocessing;

import org.apache.commons.lang3.time.DateFormatUtils;

public class Util {
	
	public static String[] dateFormats = {
		DateFormatUtils.ISO_DATE_FORMAT.getPattern(),
		DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.getPattern(),
		DateFormatUtils.ISO_DATETIME_FORMAT.getPattern(),
		DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(),
		DateFormatUtils.ISO_TIME_FORMAT.getPattern(),
		DateFormatUtils.ISO_TIME_NO_T_FORMAT.getPattern(),
		DateFormatUtils.ISO_TIME_NO_T_TIME_ZONE_FORMAT.getPattern(),
		DateFormatUtils.ISO_TIME_TIME_ZONE_FORMAT.getPattern()
	};
}

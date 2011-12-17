package dk.itu.noxdroidcloudengine.dataprocessing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.boehn.kmlframework.kml.ColorModeEnum;
import org.boehn.kmlframework.kml.LineStyle;
import org.boehn.kmlframework.kml.Style;

public class Util {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
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
	private static String HEXCOLOR_GREEN = "5000ff00";
	private static String HEXCOLOR_YELLOW = "5000ffff";
	private static String HEXCOLOR_RED = "500000ff";
	private static String styleurl_green = "transGreenLine";
	private static String styleurl_yellow = "transYellowLine";
	private static String styleurl_red = "transRedLine";
	
	public static enum KML_STYLE {
		
		GREEN(styleurl_green, HEXCOLOR_GREEN), YELLOW(styleurl_yellow, HEXCOLOR_YELLOW) , RED(styleurl_red, HEXCOLOR_RED);
		Style STYLE;
		String STYLEURL;
		private KML_STYLE(String styleurl, String color) {
			LineStyle lineStyle;
			STYLE = new Style();
			lineStyle= new LineStyle(color, ColorModeEnum.normal, 5d);
			STYLE.setLineStyle(lineStyle);
			STYLE.setId(styleurl);
			STYLEURL = styleurl;
		}
		
	}

	public static <T> T cast(Object o, Class<? extends T> c) {
		T t = null;
		try {
			t = (T) c.cast(o);
		} catch (IllegalArgumentException e) {

		}
		return t;
	}

	public static Date tryParse(String dateStr) {
		Date dt = null;
		try {
			dt = formatter.parse(dateStr);
		} catch (ParseException e) {
			dt = null;
		}
		return dt;
	}
	
	public static Date tryParse(String dateStr, SimpleDateFormat formatter) {
		Date dt = null;
		try {
			dt = formatter.parse(dateStr);
		} catch (ParseException e) {
			dt = null;
		}
		return dt;
	}
}

package dk.itu.spct.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

public class SETTING {
	public static Map<String, String> SETTINGS = new HashMap<String, String>();
	public static final String APP_PATH = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.".replace('.', File.separator.charAt(0));

	public static String IMAGEFOLDER;
	public static String SYSTEMFOLDER;
	public static int RECEIVER_PORT;
	public static String RECEIVER_HOST;
	public static int TABLETOP_PORT;
	public static String TABLETOP_HOST;
	public static String CUSTOMGESTURES;

	public static void LoadSettings() {

		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(APP_PATH + "settings.xml"));
			XMLToMap converter = new XMLToMap();

			SETTINGS = (Map<String, String>) converter.convertToMap(fis);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		}

		IMAGEFOLDER = APP_PATH + SETTINGS.get("settings.imagefolder");
		SYSTEMFOLDER = APP_PATH + SETTINGS.get("settings.systemfolder");
		RECEIVER_PORT = Integer.valueOf(SETTINGS
				.get("settings.server.receiver.port"));
		RECEIVER_HOST = SETTINGS.get("settings.server.receiver.host");
		TABLETOP_PORT = Integer.valueOf(SETTINGS
				.get("settings.server.tabletop.port"));
		TABLETOP_HOST = SETTINGS.get("settings.server.tabletop.host");
		CUSTOMGESTURES = SYSTEMFOLDER
				+ SETTINGS.get("settings.customgestures.filename");
	}
}
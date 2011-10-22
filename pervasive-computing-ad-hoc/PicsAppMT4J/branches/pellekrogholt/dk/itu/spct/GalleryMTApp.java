package dk.itu.spct;

import org.mt4j.MTApplication;

public class GalleryMTApp extends MTApplication {
	
	public static void main(String args[]) {
		initialize();
	}

	@Override
	public void startUp() {
		addScene(new GalleryScene(this, "Gallery"));
	}
}

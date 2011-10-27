package dk.itu.spct;

import java.awt.Image;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.util.MT4jSettings;

import processing.core.PImage;
import dk.itu.spct.server.AbstractServer;
import dk.itu.spct.server.MTServer;
import dk.itu.spct.util.SETTING;

public class FluidPhotoBrowser extends MTApplication {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UnistrokeProcessor global_up;
	private MTServer server1;
	private AbstractServer server2;
	
	public static void main(String args[]) {
		initialize();
	}

	@Override
	public void startUp() {
		global_up = new UnistrokeProcessor(this);
		SETTING.LoadSettings();
		
		addScene(new GalleryScene(this, "Gallery"));
		addScene(new SettingsScene(this, "Settings"));
		addScene(new ImageScene(this, "ImageView"));
		
		InetSocketAddress server2InetAddr = new InetSocketAddress(SETTING.RECEIVER_HOST,SETTING.RECEIVER_PORT);
		InetSocketAddress server1InetAddr = new InetSocketAddress(SETTING.TABLETOP_HOST,SETTING.TABLETOP_PORT);

		server1 = new MTServer(SETTING.TABLETOP_PORT, server2InetAddr, this);
		server2 = new MTServer(SETTING.RECEIVER_PORT, server1InetAddr, this);
		Thread serverThread1 = new Thread(server1);
		serverThread1.start();
		Thread serverThread2 = new Thread(server2);
		serverThread2.start();
		
//		try {
//			Image image = ImageIO.read(new File((SETTING.IMAGEFOLDER) + "chan.jpg"));
//			sendImage(image);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
	}
	

	public void sendImage(Image image) {
		try {
			server1.ExecuteAndSend(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addImage(Image image) {
		System.out.println(image == null);
		
		PImage pic = this.loadImageMT(image);
		MTImage imgFrame = new MTImage(this, pic);
		
		if (MT4jSettings.getInstance().isOpenGlMode())
			imgFrame.setUseDirectGL(true);

		imgFrame.addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());
		imgFrame.addGestureListener(ArcballProcessor.class,
				new DefaultArcballAction());
		
		imgFrame.registerInputProcessor(FluidPhotoBrowser.global_up);
		this.getScene("Gallery").getCanvas().addChild(imgFrame);
	}	
}
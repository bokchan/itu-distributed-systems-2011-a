package dk.itu.spct;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.util.MT4jSettings;

import processing.core.PImage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import dk.itu.spct.server.AbstractServer;
import dk.itu.spct.server.MTServer;

public class FluidPhotoBrowser extends MTApplication {
	public static UnistrokeProcessor global_up;
	private MTServer server1;
	private AbstractServer server2;
	Map<String, Object> SETTINGS = new HashMap<String, Object>();

	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));

	public static void main(String args[]) {
		initialize();
	}

	@Override
	public void startUp() {
		global_up = new UnistrokeProcessor(this);
		
		addScene(new GalleryScene(this, "Gallery"));
		addScene(new SettingsScene(this, "Settings"));
		addScene(new ImageScene(this, "ImageView"));
		
		LoadSettings();

		InetSocketAddress server1InetAddr = new InetSocketAddress("localhost",
				123);
		InetSocketAddress server2InetAddr = new InetSocketAddress("localhost",
				456);

		server1 = new MTServer(123, server2InetAddr, this);
		server2 = new MTServer(456, server1InetAddr, this);
		Thread serverThread1 = new Thread(server1);
		serverThread1.start();
		Thread serverThread2 = new Thread(server2);
		serverThread2.start();
		try {
			Image image = ImageIO.read((new File(imagePath + "chan.jpg")));
			sendImage(image);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
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
		System.out.println("loadimg");
		MTImage imgFrame = new MTImage(this, pic);
		
		if (MT4jSettings.getInstance().isOpenGlMode())
			imgFrame.setUseDirectGL(true);

		imgFrame.addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());
		imgFrame.addGestureListener(ArcballProcessor.class,
				new DefaultArcballAction());
		System.out.println("added listeners");
		imgFrame.registerInputProcessor(FluidPhotoBrowser.global_up);
		System.out.println(this.getSceneCount());
		
		this.getScene("Gallery").getCanvas().addChild(imgFrame);
	}
	
	void LoadSettings() {
		XStream xs = new XStream(new DomDriver());
		SETTINGS = (Map<String, Object>) xs.fromXML(new File(imagePath +  "settings.xml"));
	}
}
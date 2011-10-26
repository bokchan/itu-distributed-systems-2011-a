package dk.itu.spct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeTemplates.Template;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class SettingsScene extends AbstractScene {

	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));
	private AbstractMTApplication mtApp;

	private List<Vector3D> points;
	UnistrokeGesture CUSTOM = null;
	MTImage gesturePad;
	boolean isContained = false;
	ObjectOutputStream oos = null;

	public SettingsScene(AbstractMTApplication mtApplication, String name) {

		super(mtApplication, name);
		mtApp = mtApplication;
		points = new ArrayList<Vector3D>();

		// Settings button
		PImage img_settings = mtApplication.loadImage(imagePath
				+ "settings48x48.png");
		MTImageButton btn_settings = new MTImageButton(mtApp, img_settings);
		btn_settings.setNoStroke(true);
		btn_settings.setAnchor(PositionAnchor.UPPER_LEFT);
		if (MT4jSettings.getInstance().isOpenGlMode())
			btn_settings.setUseDirectGL(true);

		btn_settings.addGestureListener(TapProcessor.class,
				new IGestureEventListener() {

					@Override
					public boolean processGestureEvent(MTGestureEvent ge) {
						TapEvent e = (TapEvent) ge;
						if (e.isTapped()) {
							mtApp.changeScene(mtApp.getScene("Gallery"));
						}
						return true;
					}
				});

		getCanvas().addChild(btn_settings);

		/**
		 * Rectangle
		 */
		PImage pic = mtApp.loadImage(imagePath + "chan.jpg");

		gesturePad = new MTImage(mtApp, pic);
		gesturePad.setAnchor(PositionAnchor.CENTER);
		gesturePad.setPositionGlobal(new Vector3D(mtApp.width / 2,
				mtApp.height / 2));
		gesturePad.setPickable(false);
		gesturePad.setNoStroke(false);
		gesturePad.setFillColor(MTColor.WHITE);
		gesturePad.setName("GesturePad");
		
		// getCanvas().registerInputProcessor(up);
		getCanvas().registerInputProcessor(FluidPhotoBrowser.global_up);
		getCanvas().addGestureListener(UnistrokeProcessor.class,
				new IGestureEventListener() {
					@Override
					public boolean processGestureEvent(MTGestureEvent ge) {
						UnistrokeEvent ue = (UnistrokeEvent) ge;
						System.out.println("TARGET: "
								+ ue.getTarget().getName());
						switch (ge.getId()) {
						case UnistrokeEvent.GESTURE_STARTED:
							System.out.println("started");
							points.clear();
							getCanvas().addChild(ue.getVisualization());
							if (gesturePad.containsPointGlobal(ue.getCursor()
									.getPosition())) {
								isContained = true;
							}
							break;
						case UnistrokeEvent.GESTURE_UPDATED:
							points.add(ue.getCursor().getPosition());
							System.out.println(ue.getCursor().getPosition()
									.toString());
							break;
						case UnistrokeEvent.GESTURE_ENDED:
							UnistrokeGesture g = ue.getGesture();
							System.out.println("Recognized gesture: " + g);
							if (isContained) {
								addCustomGesture(
								FluidPhotoBrowser.global_up.addTemplate(
										UnistrokeGesture.CUSTOMGESTURE, points,
										Direction.CLOCKWISE)
								);
								// Create new gesture

								for (Vector3D p : points) {
									MTEllipse e = new MTEllipse(mtApp, p, 1, 1);
									e.setFillColor(MTColor.RED);
									getCanvas().addChild(e);
								}
							}

							isContained = false;

							break;
						default:
							break;
						}
						return false;
					}
				});
		getCanvas().addChild(gesturePad);
	}

	private void addCustomGesture(Template gesturetemplate) {

		System.out.println("writing xml");
		File file = new File(imagePath + "customgestures.xml");
		FileOutputStream writer;
		try {
			writer = new FileOutputStream(file,true);
			
			if (oos == null) {
				oos = new ObjectOutputStream(writer);
			}
			
			//oos.writeObject(gesturetemplate.getPoints());
			oos.writeObject(gesturetemplate);
			
			writer.flush();
			oos.flush();
			
			
//			FileInputStream in = new FileInputStream(imagePath + "customgestures.xml");
//			DataInputStream din = new DataInputStream(in);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(din));
//			String line = "";
//			
//			while ((line = reader.readLine()) != null) {
//				ByteArrayInputStream bais = new ByteArrayInputStream(line.getBytes());
//				ObjectInputStream ois = new ObjectInputStream(bais);
//				byte[] obj = (byte[]) ois.readObject();
//				
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
	
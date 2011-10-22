package dk.itu.spct;

import java.io.File;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.input.inputProcessors.componentProcessors.zoomProcessor.ZoomProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;

import processing.core.PImage;

public class GalleryScene extends AbstractScene {
	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));
	// private final String imagePath = "basic"+ AbstractMTApplication.separator
	// + "scenes" + AbstractMTApplication.separator + "data" +
	// AbstractMTApplication.separator;

	private AbstractMTApplication mtApp;
	private MTTextArea ma;

	public GalleryScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		mtApp = mtApplication;

		this.setClearColor(new MTColor(151f, 132f, 19f));

		IFont font = FontManager.getInstance().createFont(getMTApplication(),
				"arial.ttf", 35, MTColor.WHITE);
		ma = new MTTextArea(getMTApplication(), font);
		ma.setPickable(false);
		ma.setFillColor(MTColor.GREEN);
		ma.setText("Test");
		
		getCanvas().addChild(ma);

		String filename = "chan.jpg";
		// Images
		PImage pic = mtApp.loadImage(imagePath + filename);
		MTImage imgFrame = new MTImage(getMTApplication(), pic);

		if (MT4jSettings.getInstance().isOpenGlMode())
			imgFrame.setUseDirectGL(true);
		imgFrame.addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());
		imgFrame.addGestureListener(ArcballProcessor.class,
				new DefaultArcballAction());
		getCanvas().addChild(imgFrame);

		// Register input processers
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		getCanvas().registerInputProcessor(new ZoomProcessor(mtApp));
		getCanvas().addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());

		UnistrokeProcessor up = new UnistrokeProcessor(getMTApplication());
		up.addTemplate(UnistrokeGesture.CHECK, Direction.CLOCKWISE);

//		imgFrame.registerInputProcessor(up);
//		imgFrame.addGestureListener(UnistrokeProcessor.class,new IGestureEventListener() {
		getCanvas().registerInputProcessor(up);
		getCanvas().addGestureListener(UnistrokeProcessor.class,new IGestureEventListener() {
					@Override
					public boolean processGestureEvent(MTGestureEvent ge) {
						UnistrokeEvent ue = (UnistrokeEvent) ge;
						switch (ge.getId()) {
						case UnistrokeEvent.GESTURE_STARTED:
							getCanvas().addChild(ue.getVisualization());
							break;
						case UnistrokeEvent.GESTURE_UPDATED:
							break;
						case UnistrokeEvent.GESTURE_ENDED:
							UnistrokeGesture g = ue.getGesture();
							System.out.println("Recognized gesture: " + g);
							ma.setText("Recognized: " + g);
							break;
						default:
							break;

						}
						return false;
					}
				});
	}

	@Override
	public void onEnter() {
		System.out.println("entered");
	}

}

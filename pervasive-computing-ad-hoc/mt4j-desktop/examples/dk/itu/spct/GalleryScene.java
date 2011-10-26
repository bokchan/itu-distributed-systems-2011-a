package dk.itu.spct;

import java.awt.Image;
import java.io.File;

import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.gestureAction.DefaultArcballAction;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.arcballProcessor.ArcballProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
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
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class GalleryScene extends AbstractScene {
	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));

	// private final String imagePath = "basic"+ AbstractMTApplication.separator
	// + "scenes" + AbstractMTApplication.separator + "data" +
	// AbstractMTApplication.separator;

	private FluidPhotoBrowser mtApp;
	private MTTextArea ma;

	public GalleryScene(FluidPhotoBrowser mtApplication, String name) {
		super(mtApplication, name);

		mtApp = mtApplication;
		
		IFont font = FontManager.getInstance().createFont(mtApp, "arial.ttf",
				35, MTColor.WHITE);
		ma = new MTTextArea(mtApp, font);
		ma.setPickable(false);
		ma.setFillColor(MTColor.GREEN);
		ma.setText("Test");

		getCanvas().addChild(ma);

		// Settings button
		PImage img_settings = mtApplication.loadImage(imagePath
				+ "settings48x48.png");
		MTImageButton btn_settings = new MTImageButton(mtApp, img_settings);
		btn_settings.setNoStroke(true);
		btn_settings.setAnchor(PositionAnchor.UPPER_LEFT);
		if (MT4jSettings.getInstance().isOpenGlMode())
			System.out.println("isopengl");
			btn_settings.setUseDirectGL(true);

		btn_settings.addGestureListener(TapProcessor.class,
				new IGestureEventListener() {
					@Override
					public boolean processGestureEvent(MTGestureEvent ge) {
						TapEvent e = (TapEvent) ge;
						if (e.isTapped()) {
							mtApp.changeScene(mtApp.getScene("Settings"));
						}
						return true;
					}
				});

		getCanvas().addChild(btn_settings);

		String filename = "chan.jpg";
		// Images
		PImage pic = mtApp.loadImage(imagePath + filename);
		MTImage imgFrame = new MTImage(mtApp, pic);

		if (MT4jSettings.getInstance().isOpenGlMode())
			imgFrame.setUseDirectGL(true);

		imgFrame.addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());
		imgFrame.addGestureListener(ArcballProcessor.class,
				new DefaultArcballAction());
		
		imgFrame.registerInputProcessor(FluidPhotoBrowser.global_up);
		
		imgFrame.registerInputProcessor(new TapProcessor(mtApp));
//		imgFrame.addGestureListener(TapProcessor.class, new IGestureEventListener() {
//					@Override
//					public boolean processGestureEvent(MTGestureEvent ge) {
//						TapEvent e = (TapEvent) ge;
//						if (e.isTapped()) {
//							mtApp.changeScene(mtApp.getScene("ImageView"));
//						}
//						return true;
//					}
//				});
		getCanvas().addChild(imgFrame);

		// Register input processers
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		getCanvas().registerInputProcessor(new ZoomProcessor(mtApp));
		getCanvas().addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());
		
		UnistrokeProcessor up = new UnistrokeProcessor(mtApp);
		up.addTemplate(UnistrokeGesture.CHECK, Direction.CLOCKWISE);

		// IGestureEventListener() {
		getCanvas().registerInputProcessor(FluidPhotoBrowser.global_up);
		getCanvas().addGestureListener(UnistrokeProcessor.class,
				new IGestureEventListener() {
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
							MTImage img;
							if (g == UnistrokeGesture.CUSTOMGESTURE) {
								img = getTargetComponent(ue.getCursor()
										.getPosition());
								Image i = img.getImage().getTexture()
										.getImage();
								mtApp.sendImage(i);
							}
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

	private MTImage getTargetComponent(Vector3D point) {
		
		IMTComponent3D component = getCanvas().getComponentAt(
				Integer.valueOf( (int)point.x),
				Integer.valueOf((int)(point.y)));

		if (component instanceof MTImage) {
			return (MTImage) component;
		}
		
		return null;
	}
}
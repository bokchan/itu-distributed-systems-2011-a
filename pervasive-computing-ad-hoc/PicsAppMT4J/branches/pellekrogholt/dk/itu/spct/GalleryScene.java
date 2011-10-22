package dk.itu.spct;

import java.io.File;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultZoomAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
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
		
		
		
		
//		imgFrame.addGestureListener(ZoomProcessor.class,
//				new DefaultZoomAction());
//		imgFrame.addGestureListener(ArcballProcessor.class,
//				new DefaultArcballAction());
//
//		// note: lets have a listener for a simple gesture not requiring multi
//		// touch
//		imgFrame.addGestureListener(DragProcessor.class,
//				new DefaultDragAction());

		// nb! the above addGestureListener seems to have no effect - look up bellow a gesture listener seems to need much more...
		
		
		// image is added to the canvas
		getCanvas().addChild(imgFrame);

		
		
		/* 
		 * event listener for the canvas
		 *  
		 */
		
		
		// Register input processers
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		getCanvas().registerInputProcessor(new ZoomProcessor(mtApp));
		getCanvas().addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());

		UnistrokeProcessor up = new UnistrokeProcessor(getMTApplication());
		up.addTemplate(UnistrokeGesture.CHECK, Direction.CLOCKWISE);

		// imgFrame.registerInputProcessor(up);
		// imgFrame.addGestureListener(UnistrokeProcessor.class,new
		// IGestureEventListener() {
		getCanvas().registerInputProcessor(up);
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
							break;
						default:
							break;

						}
						return false;
					}
				});
	
	
//		component.registerInputProcessor(new DragProcessor(mtApplication));
//		component.addGestureListener(DragProcessor.class, new IGestureEventListener() {
//		getCanvas().registerInputProcessor(new DragProcessor(mtApplication));
//		getCanvas().addGestureListener(DragProcessor.class, new IGestureEventListener() {

		
		/* 
		 * event listener for the image
		 * 
		 * based upon: http://goo.gl/zHZ7W 
		 * 
		 * this does not use unistroke right
		 * 
		 */
		
		imgFrame.registerInputProcessor(new DragProcessor(mtApplication));
		imgFrame.addGestureListener(DragProcessor.class, new IGestureEventListener() {
			
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent)ge;
				de.getTargetComponent().translateGlobal(de.getTranslationVect()); //Moves the component
				
				switch (de.getId()) {
				case MTGestureEvent.GESTURE_STARTED:
					System.out.println("Drag gesture on component " + de.getTargetComponent() + " started");
					break;
				case MTGestureEvent.GESTURE_UPDATED:
		                        System.out.println("Drag gesture on component " + de.getTargetComponent() + " updated");
					break;
				case MTGestureEvent.GESTURE_ENDED:
					System.out.println("Drag gesture on component " + de.getTargetComponent() + " ended");
					ma.setText("Recognized: " + de.getTargetComponent());
					
					break;
				default:
					break;
				}		
				return false;
			}
		});
	
	
		// can be added simply by - but how to print out etc...
//		imgFrame.registerInputProcessor(new DragProcessor(mtApplication));
//		imgFrame.addGestureListener(DragProcessor.class, new DefaultDragAction());
	
	
	
	
	
	}

	@Override
	public void onEnter() {
		System.out.println("entered");
	}

}

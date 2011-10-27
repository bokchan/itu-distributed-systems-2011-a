package dk.itu.spct;

import java.awt.Image;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
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
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeTemplates.Template;
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
import dk.itu.spct.util.SETTING;

public class GalleryScene extends AbstractScene {

	// private final String imagePath = "basic"+ AbstractMTApplication.separator
	// + "scenes" + AbstractMTApplication.separator + "data" +
	// AbstractMTApplication.separator;

	private FluidPhotoBrowser mtApp;
	private MTTextArea ma;
	private MTRectangle menu;
	private MTRectangle gallery;
	ArrayList<MTImage> galleryimages;
	
	
	public GalleryScene(FluidPhotoBrowser mtApplication, String name) {
		
		
		super(mtApplication, name);
		System.out.println("Create Galleryscene");

		mtApp = mtApplication;

		IFont font = FontManager.getInstance().createFont(mtApp, "arial.ttf",
				35, MTColor.WHITE);
		ma = new MTTextArea(mtApp, font);
		ma.setPickable(false);
		ma.setFillColor(MTColor.GREEN);
		ma.setText("Test");

		// getCanvas().addChild(ma);
		CreateMenu();
		LoadGallery();
		LoadCustomGestures();

		// Register input processers
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		getCanvas().registerInputProcessor(new ZoomProcessor(mtApp));
		getCanvas().addGestureListener(ZoomProcessor.class,
				new DefaultZoomAction());

		UnistrokeProcessor up = new UnistrokeProcessor(mtApp);
		up.addTemplate(UnistrokeGesture.CHECK, Direction.CLOCKWISE);

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
		updateImages();
	}

	private MTImage getTargetComponent(Vector3D point) {

		IMTComponent3D component = getCanvas().getComponentAt(
				Integer.valueOf((int) point.x),
				Integer.valueOf((int) (point.y)));

		if (component instanceof MTImage) {
			return (MTImage) component;
		}

		return null;
	}

	private void LoadCustomGestures() {
		File file = new File(SETTING.SYSTEMFOLDER + "customgestures.dat");
		FileInputStream reader;

		if (file.length() > 0) {
			try {
				reader = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(reader);

				Object o = null;
				Template gesture = null;
				while (reader.available() > 0 && (o = ois.readObject()) != null) {
					// List<Vector3D> li = (List<Vector3D>) ois.readObject();
					gesture = (Template) o;
					System.out.println("read obj");

					FluidPhotoBrowser.global_up.addTemplate(
							UnistrokeGesture.CUSTOMGESTURE,
							gesture.getPoints(), Direction.CLOCKWISE);
				}
				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads all images in the imagefolder
	 */
	private void LoadGallery() {

		System.out.println("loading images");
		
		File dir = new File(SETTING.IMAGEFOLDER);

		File[] images = dir.listFiles(new GalleryFilter());
		
		galleryimages = new ArrayList<MTImage>();
		MTImage[] mtImages = new MTImage[images.length];
		// PImage pic = mtApp.loadImage(SETTING.IMAGEFOLDER + filename);
		for (File f : images) {
			PImage pic = mtApp.loadImage(f.getAbsolutePath());
			MTImage imgFrame = new MTImage(mtApp, pic);
			
			float maxLength = Math.max(pic.width, pic.height);

			float x = (float) Math.max((Math.random() * getCanvas()
					.getRenderer().getWidth()) - maxLength, maxLength);
			float y = (float) Math.max((Math.random() * getCanvas()
					.getRenderer().getHeight()) - maxLength, maxLength);
			
			Vector3D position = new Vector3D(x, y);
			
			imgFrame.setPositionRelativeToParent(position);
			imgFrame.rotateZ(position, (float) Math.random() * 360);

			if (MT4jSettings.getInstance().isOpenGlMode())
				imgFrame.setUseDirectGL(true);

			imgFrame.addGestureListener(ZoomProcessor.class,
					new DefaultZoomAction());
			imgFrame.addGestureListener(ArcballProcessor.class,
					new DefaultArcballAction());

			imgFrame.registerInputProcessor(new TapProcessor(mtApp));
			imgFrame.addGestureListener(TapProcessor.class,
					new IGestureEventListener() {

						@Override
						public boolean processGestureEvent(MTGestureEvent ge) {
							TapEvent e = (TapEvent) ge;
							if (e.isTapped()) {
								
								MTImage img = getTargetComponent(e.getCursor()
										.getPosition());
								if (img != null) {
									  
									ImageScene imgScene = (ImageScene) mtApp.getScene("ImageView"); 
									
									
									imgScene.setImage(img);
									mtApp.changeScene(imgScene);
//									mtApp.changeScene(mtApp
//											.getScene("ImageView"));
//									((ImageScene) mtApp.getScene("ImageView"))
//											.setImage(img);
								}
							}
							return true;
						}
					});

			imgFrame.registerInputProcessor(FluidPhotoBrowser.global_up);
			imgFrame.addGestureListener(UnistrokeProcessor.class,
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
			galleryimages.add(imgFrame);

		}
		
		getCanvas().addChildren((MTImage[]) galleryimages.toArray(mtImages));
	}

	private void CreateMenu() {
		// Settings button
		PImage img_settings = mtApp.loadImage(SETTING.SYSTEMFOLDER
				+ "settings48x48.png");
		MTImageButton btn_settings = new MTImageButton(mtApp, img_settings);
		btn_settings.setNoStroke(true);
		// btn_settings.setAnchor(PositionAnchor.UPPER_LEFT);
		btn_settings.setPositionRelativeToParent(new Vector3D(getCanvas()
				.getRenderer().getWidth() - 24, 24));
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
		//
		// img_settings = mtApp.loadImage(SETTING.SYSTEMFOLDER
		// + "folder_green.png");
		// btn_settings = new MTImageButton(mtApp, img_settings);
		// btn_settings.setNoStroke(true);
		// btn_settings.setPositionRelativeToParent(new Vector3D(72,24));
		// if (MT4jSettings.getInstance().isOpenGlMode())
		// System.out.println("isopengl");
		// btn_settings.setUseDirectGL(true);

		// JFrame frame =new JFrame();
		// JFileChooser fc = new JFileChooser(SETTING.IMAGEFOLDER);
		// fc.showOpenDialog(frame);
		// frame.setVisible(true);

		// menu.addChild(btn_settings);
	}

	class GalleryFilter extends ImageFilter implements FileFilter {
		Set<String> extensions = new HashSet<String>(Arrays.asList("jpg",
				"png", "gif"));

		@Override
		public boolean accept(File pathname) {

			String extension = pathname.getName().substring(
					pathname.getName().lastIndexOf(".") + 1);
			System.out.println(extension);

			return extensions.contains(extension);
		}
	}
	
	public void updateImages() {
		MTComponent[] children = getCanvas().getChildren();
		for (MTComponent c : children.clone()) {
			if (c instanceof MTImage ) {
				getCanvas().removeChild(c);
			} 
		}
		
		for (MTImage i : galleryimages) {
			getCanvas().addChild(i);
		}
	}
	
	@Override
	public void onLeave() {
	
		super.onLeave();
		mtApp.pushScene();
	}
}
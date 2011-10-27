package dk.itu.spct;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;
import dk.itu.spct.util.SETTING;

public class ImageScene extends AbstractScene {

	private AbstractMTApplication mtApp;
	private MTRectangle imageview;

	public ImageScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);

		this.mtApp = mtApplication;

		this.setClearColor(MTColor.BLACK);

		imageview = new MTRectangle(mtApp, mtApp.getWidth(), mtApp.getHeight());
		imageview.setFillColor(MTColor.BLACK);
		getCanvas().addChild(imageview);

		PImage pic = mtApp.loadImage(SETTING.SYSTEMFOLDER + "arrow-left.png");

		MTImageButton btnBack = new MTImageButton(mtApp, pic);
		btnBack.setNoStroke(true);
		btnBack.setPositionRelativeToParent(new Vector3D(30, 30));
		getCanvas().addChild(btnBack);

		btnBack.addGestureListener(TapProcessor.class,
				new IGestureEventListener() {

					@Override
					public boolean processGestureEvent(MTGestureEvent ge) {
						TapEvent e = (TapEvent) ge;
						if (e.isTapped()) {
							//mtApp.popScene();
							mtApp.changeScene(mtApp.getScene("Gallery"));
						}
						return true;
					}
				});

	}

	public void setImage(MTImage image) {
		image.setPositionRelativeToParent(imageview.getCenterPointGlobal());
		imageview.removeAllChildren();
		imageview.addChild(image);
	}
}

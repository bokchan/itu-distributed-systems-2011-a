package picsApp;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class PicsAppScene extends AbstractScene {

	public PicsAppScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		MTColor white = new MTColor(255,255,255);
		this.setClearColor(new MTColor(146, 150, 188, 255));
		//Show touches
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));

		String imagePath = "http://10.25.250.243/~stinebierre/";

		PImage show = mtApplication.loadImage(imagePath + "P2200113.jpg");

		//creates a field for the pic
		MTImage image = new MTImage(mtApplication, show); 
		
		image.setNoStroke(true);
		image.setNoFill(true);
		
		//Center the image on the screen
		image.setPositionGlobal(new Vector3D(mtApplication.width/2f, mtApplication.height/2f));
		//Add the image to our canvas
		this.getCanvas().addChild(image);
	}
	
	public void onEnter() {}
	
	public void onLeave() {}
}


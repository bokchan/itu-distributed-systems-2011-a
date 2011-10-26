package dk.itu.spct;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;

public class ImageScene extends AbstractScene {
	
	private AbstractMTApplication mtApp;

	public ImageScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		this.mtApp = mtApplication;
		
		this.setClearColor(new MTColor(129f, 151f, 239f, 0.2f));
		
	}

}

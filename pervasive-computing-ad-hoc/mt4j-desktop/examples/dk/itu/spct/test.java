package dk.itu.spct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class test {
	private final static String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileInputStream in;
		try {
			in = new FileInputStream(imagePath + "customgestures.xml");
			ObjectInputStream ois = new ObjectInputStream(in);
			Object o = null;
			
			while (in.available() > 0) {
				//List<Vector3D> li = (List<Vector3D>) ois.readObject();
				ois.readObject();
				System.out.println("read obj");
			}
			
			//System.out.println( gesture.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

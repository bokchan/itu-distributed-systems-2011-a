package dk.itu.spct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.filechooser.FileSystemView;

public class test {

	public static final String APP_PATH = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.".replace('.', File.separator.charAt(0));

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		fsv.getFiles(new File(APP_PATH), false);
		
		
		
		File file = new File(APP_PATH + "\\system\\customgestures.dat");
		FileInputStream in;
		try {
			if (file.length() > 0) {
				in = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(in);
				Object o = null;

				while (in.available() > 0) {

					// List<Vector3D> li = (List<Vector3D>) ois.readObject();
					if ((o = ois.readObject()) != null) {

						System.out.println("read obj");
					}
				}
			}

			// System.out.println( gesture.toString());
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

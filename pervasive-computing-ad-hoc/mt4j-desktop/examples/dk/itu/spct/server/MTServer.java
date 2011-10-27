package dk.itu.spct.server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

import dk.itu.spct.FluidPhotoBrowser;

public class MTServer extends AbstractServer {
	
	private InetSocketAddress receiver;
	private FluidPhotoBrowser mtApp;
	
	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));
	
	

	public MTServer() throws IOException {
		super();
	}

	public MTServer(int port, InetSocketAddress receiver,
			FluidPhotoBrowser mtApplication) {
		super(port);
		
		this.receiver = receiver;
		this.mtApp = mtApplication;
	}
	
	

	@Override
	public void ExecuteAndSend(Object obj) throws IOException {

		Image i = (Image) obj;
		BufferedImage img = toBufferedImage(i);

		Socket client = new Socket();
		try {

			client.connect(receiver);

			OutputStream os = client.getOutputStream();
			ImageIO.write(img, "jpg", os);
		} finally {
			if (client != null)
				client.close();
		}
	}

	private BufferedImage toBufferedImage(Image src) {
		int w = src.getWidth(null);
		int h = src.getHeight(null);
		int type = BufferedImage.TYPE_INT_RGB; // other options
		BufferedImage dest = new BufferedImage(w, h, type);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return dest;
	}

	@Override
	void Receive(Image i) {
		System.out.println("MTServer receive");
		System.out.println(mtApp.getSceneCount());
		mtApp.addImage(i);
		
		//save(bimage, "jpg");
	}
	
	
}
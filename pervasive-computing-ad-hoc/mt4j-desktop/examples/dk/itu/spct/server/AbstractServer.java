package dk.itu.spct.server;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

/**
 * Abstract Server class that implements runnable
 * 
 */
public abstract class AbstractServer implements Runnable {
	private final String imagePath = System.getProperty("user.dir")
			+ ".examples.dk.itu.spct.data.".replace('.',
					File.separator.charAt(0));
	private ServerSocket Listener;
	private InetSocketAddress localisa;
	private boolean trace = true;
	private String curDir = System.getProperty("user.dir");
	private String logFile = "log.log";
	private final boolean writeToLog = true;
	public static String newline = System.getProperty("line.separator");

	public LinkedList<InetSocketAddress> LocalEndpoints = new LinkedList<InetSocketAddress>();

	public AbstractServer(int port, String dir, String filename,
			boolean writeToLog, boolean printTrace) {
		curDir = dir;
		logFile = filename;
		trace = printTrace;

	}

	/***
	 * @param port
	 *            , specific port to start server on
	 * @throws IOException
	 */
	public AbstractServer(int port) {

		try {
			initServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AbstractServer() throws IOException {
		// 0 creates a server on the first available port
		initServer(0);
	}

	/***
	 * Initializes server
	 * 
	 * @param port
	 * @throws IOException
	 */
	private void initServer(int port) throws IOException {

		Listener = new ServerSocket(port);
		Listener.setSoTimeout(2000);

		Enumeration ifs = NetworkInterface.getNetworkInterfaces();
		for (; ifs.hasMoreElements();) {
			NetworkInterface nif = (NetworkInterface) ifs.nextElement();
			Enumeration addrs = nif.getInetAddresses();
			for (; addrs.hasMoreElements();) {
				InetAddress ip = (InetAddress) addrs.nextElement();
				String hostname = ip.getCanonicalHostName();
				if (!hostname.equals(ip.getHostAddress()))

					LocalEndpoints.add(new InetSocketAddress(hostname, Listener
							.getLocalPort()));
			}
		}
		// Get the servers own ip
		localisa = new InetSocketAddress(InetAddress.getLocalHost(),
				Listener.getLocalPort());
		Trace("Server started at: " + getIP());
	}

	boolean abort = false;

	static ExecutorService exeservice = Executors.newCachedThreadPool();

	public void run() {
		System.out.println("start listening");
		try {
			while (!abort) {
				try {
					final Socket client = Listener.accept();
					exeservice.execute(new Runnable() {
						public void run() {
							try {

								HandleConnection(client);
							} catch (Exception e) {
								System.err.println(e.getMessage());
								System.exit(-1);
							}
						}
					});
				} catch (SocketTimeoutException e) {
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} finally {
			try {
				Listener.close();
			} catch (IOException e) {
			}
			exeservice.shutdown();
		}
	}

	public synchronized void abort() {
		abort = true;
	}

	abstract void ExecuteAndSend(Object command) throws IOException;
	abstract void Receive(Image img);

	/**
	 * Handles incoming requests
	 * 
	 * @param client
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void HandleConnection(Socket client) throws IOException,
			ClassNotFoundException {
		System.out.println("receive");
		try {
			InputStream is = client.getInputStream();
			
			BufferedImage bimage =  ImageIO.read(is);
			Image img =  Toolkit.getDefaultToolkit().createImage(bimage.getSource());
			Receive(img);
			//save(bimage, "jpg");
			
		} finally {
			if (client != null)
				client.close();
		}
	}

	/***
	 * Returns InetSocketAddress for the server
	 * 
	 * @return
	 */
	public InetSocketAddress getIP() {
		return this.localisa;
	}

	public boolean printServerResults() {
		return trace;
	}

	public void Trace(String s) throws IOException {

		String trace = String.format("TRACE: %s\n", s);
		if (printServerResults()) {
			System.out.printf(trace);
		}
		if (writeToLog) {
			WriteLog(trace);
		}
	}

	private void WriteLog(String s) throws IOException {
		PrintWriter p = new PrintWriter(new FileWriter(curDir + "\\" + logFile,
				true));
		s.replace("\n", "\r\n");
		p.write(String.format("%s%s", s, newline));
		p.flush();
		p.close();
	}
	

	private void save(BufferedImage image, String ext) {
		String fileName = "savingAnImage";
		File file = new File(imagePath + fileName + "." + ext);
		try {
			ImageIO.write(image, ext, file); // ignore returned boolean
		} catch (IOException e) {
			System.out.println("Write error for " + file.getPath() + ": "
					+ e.getMessage());
		}
	}
}
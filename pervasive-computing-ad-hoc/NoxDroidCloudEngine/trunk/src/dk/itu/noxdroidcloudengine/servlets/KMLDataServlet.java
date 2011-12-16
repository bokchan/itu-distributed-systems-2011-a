package dk.itu.noxdroidcloudengine.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import dk.itu.noxdroidcloudengine.dataprocessing.KMLManager;

public class KMLDataServlet extends HttpServlet {
	private String CONTENTTYPE_KML = "application/vnd.google-earth.kml+xml";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String ancestorParentKeyName = req.getParameter("noxdroid");
		Key parentAncestorKey = KeyFactory.createKey("NoxDroid",
				ancestorParentKeyName);

		String ancestorKeyName = req.getParameter("track");
		Key ancestorKey = KeyFactory.createKey(parentAncestorKey, "Track",
				ancestorKeyName);
		KMLManager generator = new KMLManager();
		boolean forceCreate = false;
		String flag = req.getParameter("force_create");
		if (flag != null) {
			forceCreate = Boolean.parseBoolean(flag);
		}
		// generator.generateKML(ancestorKey);

		System.out.println("Finished");

		String kml = generator.getKML(ancestorKey, forceCreate);
		// resp.setContentType(CONTENTTYPE_KML);
		if (kml != null) {
			
			resp.setHeader("Content-type",
					"application/vnd.google-earth.kml+xml");
			resp.setHeader("Content-Disposition",
					"attachment; filename=\"" + ancestorParentKeyName +  ".kml\"");

			resp.setStatus(200);
			resp.getWriter().print(kml);
		} else {
			resp.setHeader("Content-Type", "text/plain; charset=utf-8");
			resp.setStatus(400);
			resp.getWriter().print("Something went wrong");
		}

		// ServletOutputStream stream = null;
		// BufferedInputStream buf = null;
		// ByteArrayInputStream bas = null;
		// try {
		// byte[] kmlarray = kml.getBytes();
		// stream = resp.getOutputStream();
		// bas = new ByteArrayInputStream(kmlarray);
		// buf = new BufferedInputStream(bas);
		// resp.setContentLength(kmlarray.length);
		// int readBytes = 0;
		// while ((readBytes = buf.read()) != -1) {
		// stream.write(readBytes);
		// }
		// resp.getOutputStream().write(kmlarray);
		// resp.flushBuffer();
		// // resp.getWriter().print(kml);
		// } catch (IOException ioe) {
		// throw new ServletException(ioe.getMessage());
		// } finally {
		// if (stream != null)
		// stream.close();
		// if (buf != null)
		// buf.close();
		// if (bas!= null) {
		// bas.close();
		// }
		// }
	}
}
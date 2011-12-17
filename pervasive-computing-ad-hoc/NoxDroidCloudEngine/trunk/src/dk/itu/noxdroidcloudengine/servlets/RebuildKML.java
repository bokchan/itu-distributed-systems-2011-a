package dk.itu.noxdroidcloudengine.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dk.itu.noxdroidcloudengine.dataprocessing.KMLManager;

public class RebuildKML extends HttpServlet {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd");
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Date date = null;
		String dateStr = req.getParameter("date");
		try {
			date =formatter.parse(dateStr);
		} catch (ParseException e) {
			
		}
		KMLManager manager = new KMLManager();
		manager.rebuildKML(date, date);
		
		resp.getWriter().print("Finished");
	}

}

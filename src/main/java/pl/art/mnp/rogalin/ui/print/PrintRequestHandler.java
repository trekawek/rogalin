package pl.art.mnp.rogalin.ui.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.PathUtils;
import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;

import com.mongodb.DBObject;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

public class PrintRequestHandler implements RequestHandler {

	private static final long serialVersionUID = -2275593240545501411L;

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
			throws IOException {
		if ("/print".equals(request.getPathInfo())) {
			renderOutput(request, response);
			return true;
		} else
			return false;
	}

	private void renderOutput(VaadinRequest request, VaadinResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");

		final PrintWriter writer = response.getWriter();
		writer.write(getTemplate("header.html"));

		ObjectId objectId = new ObjectId(request.getParameter("objectId"));
		DBObject dbObject = DbConnection.getInstance().getObjectsDao().getObject(objectId);

		writer.append(new ObjectRenderer(dbObject).toString());
		writer.append("<div class=\"images\">");

		for (DbPhoto p : DbConnection.getInstance().getObjectsDao().getPhotos(dbObject)) {
			writer.append(renderPhoto(p));
		}

		writer.append("</div>");
		writer.write(getTemplate("footer.html"));
	}

	private String renderPhoto(DbPhoto p) {
		StringBuilder builder = new StringBuilder();
		builder.append("<img src=\"");
		builder.append(getPhotoUrl(p));
		builder.append("\"/>");
		return builder.toString();
	}

	private String getPhotoUrl(DbPhoto p) {
		return PathUtils.getServletPath() + "/image?photoId=" + p.getPhotoId().toString();
	}

	private String getTemplate(String name) throws IOException {
		InputStream is = VaadinServlet.getCurrent().getServletContext()
				.getResourceAsStream(String.format("/VAADIN/print/%s", name));
		try {
			return IOUtils.toString(is);
		} finally {
			is.close();
		}
	}
}

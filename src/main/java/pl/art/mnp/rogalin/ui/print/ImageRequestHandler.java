package pl.art.mnp.rogalin.ui.print;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.db.DbConnection;

import com.mongodb.gridfs.GridFSDBFile;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

public class ImageRequestHandler implements RequestHandler {

	private static final long serialVersionUID = 2586966503477375519L;

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
			throws IOException {
		if ("/image".equals(request.getPathInfo())) {
			renderImage(request, response);
			return true;
		} else
			return false;
	}

	private void renderImage(VaadinRequest request, VaadinResponse response) throws IOException {
		ObjectId objectId = new ObjectId(request.getParameter("photoId"));
		GridFSDBFile photo = DbConnection.getInstance().getGridFS().findOne(objectId);

		response.setContentType(photo.getContentType());
		InputStream is = photo.getInputStream();
		try {
			IOUtils.copy(is, response.getOutputStream());
		} finally {
			is.close();
		}
	}
}

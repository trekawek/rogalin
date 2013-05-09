package pl.art.mnp.rogalin.ui.tab.object.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

@SuppressWarnings("serial")
public class DbPhoto implements PhotoModel {

	private final GridFSDBFile file;

	private final PhotoType type;

	private final ObjectId objectId;

	public DbPhoto(DBObject photo, GridFS gridFS) {
		objectId = (ObjectId) photo.get("file_id");
		file = gridFS.findOne(new BasicDBObject("_id", objectId));
		type = PhotoType.valueOf((String) photo.get("type"));
	}

	public PhotoType getType() {
		return type;
	}

	@Override
	public Resource getResource() {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				return file.getInputStream();
			}
		}, file.getFilename());
	}

	@Override
	public String getFileName() {
		return file.getFilename();
	}

	@Override
	public ObjectId getFileId(GridFS gridFS) throws FileNotFoundException {
		return objectId;
	}
}

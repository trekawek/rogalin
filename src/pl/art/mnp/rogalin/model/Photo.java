package pl.art.mnp.rogalin.model;

import java.io.InputStream;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.vaadin.server.StreamResource.StreamSource;

import pl.art.mnp.rogalin.ui.tab.object.PhotoType;

@SuppressWarnings("serial")
public class Photo implements StreamSource {

	private final GridFSDBFile file;

	private final PhotoType type;

	public Photo(DBObject photo, GridFS gridFS) {
		ObjectId objectId = (ObjectId) photo.get("file_id");
		file = gridFS.findOne(new BasicDBObject("_id", objectId));
		type = PhotoType.valueOf((String) photo.get("type"));
	}

	public InputStream getStream() {
		return file.getInputStream();
	}

	public String getName() {
		return file.getFilename();
	}

	public PhotoType getType() {
		return type;
	}
}

package pl.art.mnp.rogalin.model;

import java.io.InputStream;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import pl.art.mnp.rogalin.ui.tab.object.PhotoType;

public class Photo {

	private final GridFSDBFile file;

	private final PhotoType type;

	public Photo(DBObject photo, GridFS gridFS) {
		ObjectId objectId = (ObjectId) photo.get("_id");

		DBObject ref = new BasicDBObject();
		ref.put("_id", objectId);

		file = gridFS.findOne(ref);
		type = PhotoType.valueOf((String) photo.get("type"));
	}

	public InputStream getStream() {
		return file.getInputStream();
	}

	public String getName() {
		return file.getFilename();
	}

	public String getMimeType() {
		return file.getContentType();
	}

	public PhotoType getType() {
		return type;
	}
}

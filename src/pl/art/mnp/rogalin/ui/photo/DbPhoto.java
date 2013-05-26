package pl.art.mnp.rogalin.ui.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.db.DbConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;

@SuppressWarnings("serial")
public class DbPhoto implements PhotoModel, Serializable {

	private final PhotoType type;

	private final String references;

	private final String fileName;

	private final ObjectId photoId;

	private final ObjectId thumbnailId;

	public DbPhoto(DBObject dbObject) throws FileNotFoundException {
		references = dbObject.get("references").toString();
		type = PhotoType.valueOf((String) dbObject.get("type"));
		photoId = (ObjectId) getFileReferences().get("photo_id");
		thumbnailId = (ObjectId) getFileReferences().get("thumbnail_id");

		GridFS gridFS = DbConnection.getInstance().getGridFS();
		fileName = gridFS.findOne(getThumbnailReference()).getFilename();
	}

	@Override
	public PhotoType getType() {
		return type;
	}

	@Override
	public Resource getResource() {
		GridFS gridFS = DbConnection.getInstance().getGridFS();
		return new GridFsFileSource(gridFS.findOne(getPhotoReference()));
	}

	@Override
	public Resource getThumbnailResource() {
		GridFS gridFS = DbConnection.getInstance().getGridFS();
		return new GridFsFileSource(gridFS.findOne(getThumbnailReference()));
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public DBObject getFileReferences() {
		return (DBObject) JSON.parse(references);
	}

	@Override
	public void remove() {
		GridFS gridFs = DbConnection.getInstance().getGridFS();
		gridFs.remove(getPhotoReference());
		gridFs.remove(getThumbnailReference());
	}

	@Override
	public void cleanup() {
	}

	private static class GridFsFileSource extends StreamResource {
		public GridFsFileSource(final GridFSDBFile file) {
			super(new StreamSource() {
				@Override
				public InputStream getStream() {
					return file.getInputStream();
				}
			}, file.getFilename());
		}
	}

	private DBObject getPhotoReference() {
		return new BasicDBObject("_id", photoId);
	}

	private DBObject getThumbnailReference() {
		return new BasicDBObject("_id", thumbnailId);
	}

}

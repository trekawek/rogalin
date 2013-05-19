package pl.art.mnp.rogalin.ui.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;

import pl.art.mnp.rogalin.db.MongoDbProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;

@SuppressWarnings("serial")
public class DbPhoto implements PhotoModel {

	private final GridFSDBFile file;

	private GridFSDBFile thumbnail;

	private final PhotoType type;

	private final DBObject references;

	private final MongoDbProvider dbProvider;

	public DbPhoto(DBObject photo, MongoDbProvider dbProvider) throws FileNotFoundException {
		this.dbProvider = dbProvider;

		references = (DBObject) photo.get("references");

		GridFS gridFS = dbProvider.getGridFS();
		file = gridFS.findOne(new BasicDBObject("_id", references.get("photo_id")));
		thumbnail = gridFS.findOne(new BasicDBObject("_id", references.get("thumbnail_id")));

		if (file == null) {
			throw new FileNotFoundException();
		}
		if (thumbnail == null) {
			thumbnail = file;
		}
		type = PhotoType.valueOf((String) photo.get("type"));
	}

	@Override
	public PhotoType getType() {
		return type;
	}

	@Override
	public Resource getResource() {
		return new GridFsFileSource(file);
	}

	@Override
	public Resource getThumbnailResource() {
		return new GridFsFileSource(thumbnail);
	}

	@Override
	public String getFileName() {
		return file.getFilename();
	}

	@Override
	public DBObject getFileReferences() {
		return references;
	}

	@Override
	public void remove() {
		GridFS gridFs = dbProvider.getGridFS();
		gridFs.remove(new BasicDBObject("_id", file.getId()));
		gridFs.remove(new BasicDBObject("_id", thumbnail.getId()));
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
}

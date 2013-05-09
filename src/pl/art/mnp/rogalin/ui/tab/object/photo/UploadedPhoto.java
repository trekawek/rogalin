package pl.art.mnp.rogalin.ui.tab.object.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.coobird.thumbnailator.Thumbnails;

import pl.art.mnp.rogalin.db.MongoDbProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSInputFile;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

@SuppressWarnings("serial")
public class UploadedPhoto implements Serializable, PhotoModel {

	private static final Logger LOG = Logger.getLogger(UploadedPhoto.class.getName());

	static enum State {
		FINISHED, FAILED, IN_PROGRESS;
	};

	private final File file;

	private File thumbnail;

	private final String fileName;

	private final String mimeType;

	private final MongoDbProvider dbProvider;

	private State state;

	public UploadedPhoto(String fileName, String mimeType, MongoDbProvider dbProvider) throws IOException {
		this.file = File.createTempFile("rogalin", fileName);
		this.thumbnail = File.createTempFile("rogalin_thumb", fileName);
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.state = State.IN_PROGRESS;
		this.dbProvider = dbProvider;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		if (state == State.FINISHED) {
			try {
				Thumbnails.of(file).width(150).toFile(thumbnail);
			} catch (IOException e) {
				LOG.log(Level.WARNING, "Can't create thumbnail", e);
				thumbnail = file;
			}
		}
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	@Override
	public Resource getResource() {
		return new FileResource(file);
	}

	@Override
	public Resource getThumbnailResource() {
		return new FileResource(thumbnail);
	}

	@Override
	public DBObject getFileReferences() throws IOException {
		GridFSInputFile inputFile = dbProvider.getGridFS().createFile(file);
		inputFile.setFilename(fileName);
		inputFile.setContentType(mimeType);
		inputFile.save();

		GridFSInputFile thumbnailFile = dbProvider.getGridFS().createFile(thumbnail);
		thumbnailFile.setFilename("thumb_" + fileName);
		thumbnailFile.setContentType(mimeType);
		thumbnailFile.save();

		DBObject o = new BasicDBObject();
		o.put("photo_id", inputFile.getId());
		o.put("thumbnail_id", thumbnailFile.getId());
		return o;
	}

	@Override
	public void remove() {
		if (file.exists()) {
			file.delete();
		}
		if (thumbnail.exists()) {
			thumbnail.delete();
		}
	}

	@Override
	public PhotoType getType() {
		return PhotoType.CURRENT;
	}

	@Override
	public void cleanup() {
		remove();
	}

}

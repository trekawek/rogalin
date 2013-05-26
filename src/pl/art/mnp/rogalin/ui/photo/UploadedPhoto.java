package pl.art.mnp.rogalin.ui.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.coobird.thumbnailator.Thumbnails;

import pl.art.mnp.rogalin.db.DbConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

@SuppressWarnings("serial")
public class UploadedPhoto implements Serializable, PhotoModel {

	private static final Logger LOG = Logger.getLogger(UploadedPhoto.class.getName());

	static enum State {
		FINISHED, FAILED, IN_PROGRESS;
	};

	private final File rawFile;

	private final File photoFile;

	private final File thumbnailFile;

	private final String fileName;

	private final String mimeType;

	public UploadedPhoto(String fileName, String mimeType) throws IOException {
		this.rawFile = File.createTempFile("rogalin_raw", fileName);
		this.photoFile = File.createTempFile("rogalin_normal", fileName);
		this.thumbnailFile = File.createTempFile("rogalin_thumb", fileName);
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	public boolean createThumbnails() {
		try {
			Thumbnails.of(rawFile).size(150, 111).toFile(thumbnailFile);
			Thumbnails.of(rawFile).size(1024, 768).toFile(photoFile);
			return true;
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Can't create thumbnails", e);
			return false;
		}
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(rawFile);
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
		return new FileResource(photoFile);
	}

	@Override
	public Resource getThumbnailResource() {
		return new FileResource(thumbnailFile);
	}

	@Override
	public DBObject getFileReferences() throws IOException {
		GridFS gridFS = DbConnection.getInstance().getGridFS();
		GridFSInputFile photoGridFile = gridFS.createFile(photoFile);
		photoGridFile.setFilename(fileName);
		photoGridFile.setContentType(mimeType);
		photoGridFile.save();

		GridFSInputFile thumbnailGridFile = gridFS.createFile(thumbnailFile);
		thumbnailGridFile.setFilename("thumb_" + fileName);
		thumbnailGridFile.setContentType(mimeType);
		thumbnailGridFile.save();

		DBObject o = new BasicDBObject();
		o.put("photo_id", photoGridFile.getId());
		o.put("thumbnail_id", thumbnailGridFile.getId());
		return o;
	}

	@Override
	public void remove() {
		for (File f : new File[] { rawFile, photoFile, thumbnailFile }) {
			if (f.exists()) {
				f.delete();
			}
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

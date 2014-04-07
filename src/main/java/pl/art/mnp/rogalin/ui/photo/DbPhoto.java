package pl.art.mnp.rogalin.ui.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import net.coobird.thumbnailator.Thumbnails;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.db.DbConnection;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;

@SuppressWarnings("serial")
public class DbPhoto implements PhotoModel, Serializable {

	private final PhotoType type;

	private final String references;

	private final String fileName;

	private final String thumbnailFileName;

	private final ObjectId photoId;

	private final ObjectId thumbnailId;

	public DbPhoto(DBObject dbObject) throws FileNotFoundException {
		references = dbObject.get("references").toString();
		type = PhotoType.valueOf((String) dbObject.get("type"));
		photoId = (ObjectId) getFileReferences().get("photo_id");
		thumbnailId = (ObjectId) getFileReferences().get("thumbnail_id");

		GridFS gridFS = DbConnection.getInstance().getGridFS();
		fileName = gridFS.findOne(photoId).getFilename();
		thumbnailFileName = gridFS.findOne(thumbnailId).getFilename();
	}

	@Override
	public PhotoType getType() {
		return type;
	}

	@Override
	public Resource getResource() {
		return new GridFsFileSource(photoId, fileName);
	}

	@Override
	public Resource getThumbnailResource() {
		return new GridFsFileSource(thumbnailId, thumbnailFileName);
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
		gridFs.remove(photoId);
		gridFs.remove(thumbnailId);
	}

	@Override
	public void cleanup() {
	}

	private static class GridFsFileSource extends StreamResource {
		public GridFsFileSource(final ObjectId fileId, final String filename) {
			super(new StreamSource() {
				@Override
				public InputStream getStream() {
					return DbConnection.getInstance().getGridFS().find(fileId).getInputStream();
				}
			}, filename);
		}
	}

	public void generateThumbnail() throws IOException {
		GridFS gridFS = DbConnection.getInstance().getGridFS();
		GridFSDBFile photoFile = gridFS.find(photoId);

		GridFSInputFile newThumbnail = gridFS.createFile();
		newThumbnail.setFilename("thumb_" + photoFile.getFilename());
		newThumbnail.setContentType(photoFile.getContentType());
		newThumbnail.setId(thumbnailId);
		gridFS.remove(thumbnailId);

		OutputStream os = newThumbnail.getOutputStream();
		Thumbnails.of(photoFile.getInputStream())
				.size(UploadedPhoto.THUMBNAIL_WIDTH, UploadedPhoto.THUMBNAIL_HEIGHT).toOutputStream(os);
		os.close();
	}

	@Override
	public void rotate(int degrees) throws IOException {
		GridFS gridFS = DbConnection.getInstance().getGridFS();
		GridFSDBFile oldPhoto = gridFS.find(photoId);

		String contentType = oldPhoto.getContentType();
		File temp = File.createTempFile("rogalin_photo", fileName);
		Thumbnails.of(oldPhoto.getInputStream()).scale(1).rotate(degrees * 90).toFile(temp);
		gridFS.remove(photoId);
		gridFS.remove(thumbnailId);

		GridFSInputFile newPhoto = gridFS.createFile(temp);
		newPhoto.setFilename(fileName);
		newPhoto.setContentType(contentType);
		newPhoto.setId(photoId);
		newPhoto.save();

		GridFSInputFile newThumbnail = gridFS.createFile();
		newThumbnail.setFilename("thumb_" + fileName);
		newThumbnail.setContentType(contentType);
		newThumbnail.setId(thumbnailId);
		OutputStream os = newThumbnail.getOutputStream();
		Thumbnails.of(temp).size(UploadedPhoto.THUMBNAIL_WIDTH, UploadedPhoto.THUMBNAIL_HEIGHT)
				.toOutputStream(os);
		os.close();

		temp.delete();
	}
}

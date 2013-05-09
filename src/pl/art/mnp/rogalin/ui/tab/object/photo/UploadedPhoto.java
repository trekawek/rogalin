package pl.art.mnp.rogalin.ui.tab.object.photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;

@SuppressWarnings("serial")
public class UploadedPhoto implements Serializable, PhotoModel {

	static enum State {
		FINISHED, FAILED, IN_PROGRESS;
	};

	private final File file;

	private final String fileName;

	private final String mimeType;

	private State state;

	public UploadedPhoto(String fileName, String mimeType) throws IOException {
		this.file = File.createTempFile("rogalin", fileName);
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.state = State.IN_PROGRESS;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public OutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public Resource getResource() {
		return new FileResource(file);
	}

	public ObjectId getFileId(GridFS gridFS) throws FileNotFoundException {
		GridFSInputFile inputFile = gridFS.createFile(new FileInputStream(file));
		inputFile.setFilename(fileName);
		inputFile.setContentType(mimeType);
		inputFile.save();

		return (ObjectId) inputFile.getId();
	}

}

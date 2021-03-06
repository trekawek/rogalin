package pl.art.mnp.rogalin.ui.photo;

import java.io.IOException;

import com.mongodb.DBObject;
import com.vaadin.server.Resource;

public interface PhotoModel {

	Resource getResource();

	Resource getThumbnailResource();

	String getFileName();

	void remove();

	void cleanup();

	PhotoType getType();

	DBObject getFileReferences() throws IOException;

	void rotate(int degrees) throws IOException;
}

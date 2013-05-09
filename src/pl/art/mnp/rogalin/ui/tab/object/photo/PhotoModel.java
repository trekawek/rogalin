package pl.art.mnp.rogalin.ui.tab.object.photo;

import java.io.FileNotFoundException;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFS;
import com.vaadin.server.Resource;

public interface PhotoModel {

	Resource getResource();

	String getFileName();

	ObjectId getFileId(GridFS gridFS) throws FileNotFoundException;

}

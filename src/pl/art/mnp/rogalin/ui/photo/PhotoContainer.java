package pl.art.mnp.rogalin.ui.photo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.db.DbConnection;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class PhotoContainer extends VerticalLayout implements Receiver, FinishedListener, FailedListener,
		ImageRemovedListener {

	private static final Logger LOG = Logger.getLogger(PhotoContainer.class.getName());

	private final GridLayout imageContainer;

	private final Map<String, PhotoModel> images = new LinkedHashMap<String, PhotoModel>();

	private final Map<String, UploadedPhoto> tempImages = new LinkedHashMap<String, UploadedPhoto>();

	private Map<String, PhotoComponent> components;

	private Label imageLabel;

	public PhotoContainer(DBObject object) {
		super();
		this.components = Collections.emptyMap();
		imageLabel = new Label("Fotografie");
		imageLabel.addStyleName(Runo.LABEL_H2);
		imageLabel.setSizeUndefined();
		imageLabel.setVisible(false);
		addComponent(imageLabel);

		imageContainer = new GridLayout(4, 1);
		imageContainer.setSpacing(true);
		addComponent(imageContainer);

		Label title = new Label("Dodaj fotografię");
		title.addStyleName(Runo.LABEL_H2);
		title.setSizeUndefined();
		addComponent(title);

		Upload upload = new Upload();
		upload.setReceiver(this);
		upload.addFinishedListener(this);
		upload.addFailedListener(this);
		addComponent(upload);

		if (object != null) {
			showExistingImages(DbConnection.getInstance().getObjectsDao().getPhotos(object));
			updateImages();
		}
	}

	private void showExistingImages(List<DbPhoto> photos) {
		for (DbPhoto p : photos) {
			images.put(p.getFileName(), p);
		}
	}

	public void clear() {
		images.clear();
		updateImages();
	}

	public BasicDBList serializePhotos() {
		BasicDBList objects = new BasicDBList();
		if (components != null) {
			for (PhotoComponent component : components.values()) {
				try {
					objects.add(component.serializeToMongo());
					component.cleanup();
				} catch (IOException e) {
					LOG.log(Level.WARNING, "Can't save image", e);
				}
			}
		}
		return objects;
	}

	private void updateImages() {
		imageContainer.removeAllComponents();
		boolean imageDisplayed = false;
		Map<String, PhotoComponent> photoComponents = new HashMap<String, PhotoComponent>();
		for (PhotoModel image : images.values()) {
			imageDisplayed = true;
			PhotoComponent component = null;
			component = components.get(image.getFileName());
			if (component == null) {
				component = new PhotoComponent(this, image);
			}
			imageContainer.addComponent(component);
			photoComponents.put(image.getFileName(), component);
		}
		components = photoComponents;
		imageLabel.setVisible(imageDisplayed);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		try {
			UploadedPhoto uploaded = new UploadedPhoto(filename, mimeType);
			tempImages.put(filename, uploaded);
			components.remove(filename);
			return uploaded.getOutputStream();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Can't receive upload", e);
			return null;
		}
	}

	@Override
	public void uploadFinished(FinishedEvent event) {
		UploadedPhoto photo = tempImages.remove(event.getFilename());
		if (photo.createThumbnails()) {
			images.put(photo.getFileName(), photo);
			updateImages();
		} else {
			Notification.show("Nie można zapisać fotografii", Type.ERROR_MESSAGE);
		}
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		tempImages.remove(event.getFilename());
	}

	@Override
	public void imageRemoved(PhotoModel image) {
		if (images.remove(image.getFileName()) != null) {
			updateImages();
		}
	}
}
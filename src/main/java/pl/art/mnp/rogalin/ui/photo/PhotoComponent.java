package pl.art.mnp.rogalin.ui.photo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class PhotoComponent extends VerticalLayout {

	private static final Logger LOG = Logger.getLogger(PhotoComponent.class.getName());

	private final ComboBox typeSelection;

	private final PhotoModel photoModel;

	private Image image;

	public PhotoComponent(final ImageRemovedListener listener, final PhotoModel photoModel) {
		super();
		this.photoModel = photoModel;
		image = new Image(photoModel.getFileName(), photoModel.getThumbnailResource());
		addComponent(image);

		typeSelection = new ComboBox("Rodzaj");
		typeSelection.addItem(PhotoType.CURRENT);
		typeSelection.addItem(PhotoType.HISTORIC);
		typeSelection.setValue(PhotoType.CURRENT);
		typeSelection.setTextInputAllowed(false);
		typeSelection.setRequired(true);
		typeSelection.setNullSelectionAllowed(false);
		typeSelection.setImmediate(true);
		typeSelection.setValue(photoModel.getType());
		addComponent(typeSelection);

		HorizontalLayout buttons = new HorizontalLayout();
		Button button = new Button("✕");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.imageRemoved(photoModel);
				photoModel.remove();
			}
		});
		buttons.addComponent(button);

		button = new Button("⟳");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				rotateImage(1);
			}
		});
		buttons.addComponent(button);

		button = new Button("⟲");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				rotateImage(-1);
			}
		});
		buttons.addComponent(button);
		addComponent(buttons);
	}

	public DBObject serializeToMongo() throws IOException {
		DBObject object = new BasicDBObject();
		DBObject fileId = photoModel.getFileReferences();
		object.put("references", fileId);
		object.put("type", ((PhotoType) typeSelection.getValue()).name());
		return object;
	}

	public void deserialize(DBObject o) {
		typeSelection.setValue(o.get("type"));
	}

	public void cleanup() {
		photoModel.cleanup();
	}

	private void rotateImage(int direction) {
		try {
			photoModel.rotate(direction);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Can't rotate photos", e);
		}
		Image newImage = new Image(photoModel.getFileName(), photoModel.getThumbnailResource());
		replaceComponent(image, newImage);
		image = newImage;
	}
}
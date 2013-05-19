package pl.art.mnp.rogalin.ui.photo;

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class PhotoComponent extends VerticalLayout {

	private final ComboBox typeSelection;

	private final PhotoModel photoModel;

	public PhotoComponent(final ImageRemovedListener listener, final PhotoModel photoModel) {
		super();
		this.photoModel = photoModel;
		Resource resource = photoModel.getThumbnailResource();
		Image image = new Image(photoModel.getFileName(), resource);
		image.setWidth("150px");
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

		Button button = new Button("Usuń");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.imageRemoved(photoModel);
				photoModel.remove();
			}
		});
		addComponent(button);
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
}
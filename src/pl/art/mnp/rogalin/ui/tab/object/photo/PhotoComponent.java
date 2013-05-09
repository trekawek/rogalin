package pl.art.mnp.rogalin.ui.tab.object.photo;

import java.io.FileNotFoundException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
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
		Resource resource = photoModel.getResource();
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
		addComponent(typeSelection);

		Button button = new Button("Usuń");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.imageRemoved(photoModel);
			}
		});
		addComponent(button);
	}

	public DBObject serializeToMongo(GridFS gridFS) throws FileNotFoundException {
		DBObject object = new BasicDBObject();
		ObjectId fileId = photoModel.getFileId(gridFS);
		object.put("file_id", fileId);
		object.put("type", ((PhotoType) typeSelection.getValue()).name());
		return object;
	}

}

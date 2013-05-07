package pl.art.mnp.rogalin.ui.tab.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class UploadedImage implements Serializable {

	static enum State {
		FINISHED, FAILED, IN_PROGRESS;
	};

	private final File file;

	private final String fileName;

	private final String mimeType;

	private State state;

	private Layout layout;

	private ComboBox typeSelection;

	public UploadedImage(String fileName, String mimeType) throws IOException {
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

	public Component getImageComponent(final ImageRemovedListener listener) {
		if (layout != null) {
			return layout;
		}
		layout = new VerticalLayout();

		Resource resource = new FileResource(file);
		Image image = new Image(fileName, resource);
		image.setWidth("150px");
		layout.addComponent(image);

		typeSelection = new ComboBox("Rodzaj");
		typeSelection.addItem(PhotoType.CURRENT);
		typeSelection.addItem(PhotoType.HISTORIC);
		typeSelection.setValue(PhotoType.CURRENT);
		typeSelection.setTextInputAllowed(false);
		typeSelection.setRequired(true);
		typeSelection.setNullSelectionAllowed(false);
		typeSelection.setImmediate(true);
		layout.addComponent(typeSelection);

		Button button = new Button("Usuń");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.imageRemoved(fileName);
			}
		});
		layout.addComponent(button);

		return layout;
	}

	public Object serializeToMongo(GridFS gridFS) throws FileNotFoundException {
		DBObject object = new BasicDBObject();

		GridFSInputFile inputFile = gridFS.createFile(new FileInputStream(file));
		inputFile.setFilename(fileName);
		inputFile.setContentType(mimeType);
		inputFile.save();

		object.put("file_id", inputFile.getId());
		object.put("type", ((PhotoType) typeSelection.getValue()).name());
		return object;
	}
}

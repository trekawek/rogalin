package pl.art.mnp.rogalin.ui.tab.object;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.Field;
import pl.art.mnp.rogalin.ui.field.UiField;
import pl.art.mnp.rogalin.ui.tab.object.UploadedImage.State;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ObjectTab extends VerticalLayout implements Receiver, FinishedListener, FailedListener,
		ImageRemovedListener {

	private static final Logger LOG = Logger.getLogger(ObjectTab.class.getName());

	private final List<UiField> fields = new ArrayList<UiField>(Field.values().length);

	private final Map<String, UploadedImage> images = new LinkedHashMap<String, UploadedImage>();

	private final Map<Field, Label> errorLabels = new EnumMap<Field, Label>(Field.class);

	private final MongoDbProvider dbProvider;
	
	private final TabSheet tabs;

	private GridLayout imageContainer;

	private Label imageLabel;

	public ObjectTab(MongoDbProvider dbProvider, TabSheet tabs) {
		super();
		this.dbProvider = dbProvider;
		this.tabs = tabs;

		setMargin(true);
		GridLayout columns = new GridLayout(2, 1);
		columns.setSpacing(true);
		FormLayout leftColumn = new FormLayout();
		FormLayout rightColumn = new FormLayout();
		FormLayout belowColumns = new FormLayout();
		columns.addComponent(leftColumn, 0, 0);
		columns.addComponent(rightColumn, 1, 0);
		addComponent(columns);
		addComponent(belowColumns);

		int i = 0;
		for (Field f : Field.values()) {
			UiField uiField = f.getUiField(dbProvider);
			fields.add(uiField);

			Label errorLabel = new Label();
			errorLabel.setStyleName(Runo.LABEL_SMALL);
			errorLabel.setVisible(false);
			errorLabel.setContentMode(ContentMode.HTML);

			Layout container;
			if (f.isBelowColumns()) {
				container = belowColumns;
			} else if (i <= 19) {
				container = leftColumn;
				i++;
			} else {
				container = rightColumn;
			}
			container.addComponent(uiField.getComponent());
			container.addComponent(errorLabel);
			errorLabels.put(f, errorLabel);
		}

		Button button = new Button("Zapisz obiekt");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addObject();
			}
		});
		addComponent(button);

		imageLabel = new Label("Zdjęcia");
		imageLabel.addStyleName(Runo.LABEL_H2);
		imageLabel.setSizeUndefined();
		imageLabel.setVisible(false);
		addComponent(imageLabel);

		imageContainer = new GridLayout(4, 1);
		imageContainer.setSpacing(true);
		addComponent(imageContainer);

		Label title = new Label("Dodaj zdjęcie");
		title.addStyleName(Runo.LABEL_H2);
		title.setSizeUndefined();
		addComponent(title);

		Upload upload = new Upload();
		upload.setReceiver(this);
		upload.addFinishedListener(this);
		upload.addFailedListener(this);
		addComponent(upload);
	}

	private void addObject() {
		boolean validated = true;
		for (UiField field : fields) {
			// Label errorLabel = errorLabels.get(field.getFieldInfo());
			try {
				field.validate();
				// errorLabel.setVisible(false);
			} catch (InvalidValueException e) {
				validated = false;
				// errorLabel.setCaption(e.getMessage());
				// errorLabel.setVisible(true);
			}
		}
		if (validated) {
			dbProvider.getObjectsProvider().addNewObject(fields, images.values());
			Notification.show("Zapisano obiekt", Type.HUMANIZED_MESSAGE);
		} else {
			Notification.show("Uzupełnij wszystkie wymagane pola.", Type.ERROR_MESSAGE);
		}
		tabs.setSelectedTab(0);
	}

	private void updateImages() {
		imageContainer.removeAllComponents();
		boolean imageDisplayed = false;
		for (UploadedImage uploadedImage : images.values()) {
			if (uploadedImage.getState() == State.FINISHED) {
				imageDisplayed = true;
				imageContainer.addComponent(uploadedImage.getImageComponent(this));
			}
		}
		imageLabel.setVisible(imageDisplayed);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		try {
			UploadedImage uploaded = new UploadedImage(filename, mimeType);
			images.put(filename, uploaded);
			return uploaded.getOutputStream();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Can't receive upload", e);
			return null;
		}
	}

	@Override
	public void uploadFinished(FinishedEvent event) {
		images.get(event.getFilename()).setState(State.FINISHED);
		updateImages();
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		images.get(event.getFilename()).setState(State.FAILED);
	}

	@Override
	public void imageRemoved(String filename) {
		if (images.remove(filename) != null) {
			updateImages();
		}
	}
}

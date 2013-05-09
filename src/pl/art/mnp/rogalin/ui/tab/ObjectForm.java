package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.List;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.FieldInfo;
import pl.art.mnp.rogalin.ui.field.UiField;
import pl.art.mnp.rogalin.ui.tab.object.photo.PhotoContainer;

import com.mongodb.DBObject;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ObjectForm extends VerticalLayout {

	private final List<UiField> fields = new ArrayList<UiField>(FieldInfo.values().length);

	private final PhotoContainer photoContainer;

	private final MongoDbProvider dbProvider;

	private final Runnable saveAction;

	public ObjectForm(MongoDbProvider dbProvider, Runnable saveAction) {
		this(dbProvider, saveAction, null);
	}

	public ObjectForm(MongoDbProvider dbProvider, Runnable saveAction, DBObject object) {
		super();
		this.dbProvider = dbProvider;
		this.saveAction = saveAction;

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
		for (FieldInfo f : FieldInfo.values()) {
			UiField uiField = f.getUiField(dbProvider);
			uiField.deserializeFromMongo(object);
			fields.add(uiField);

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
		}

		photoContainer = new PhotoContainer(dbProvider.getObjectsProvider(), object);
		addComponent(photoContainer);

		Button button = new Button("Zapisz obiekt");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addObject();
			}
		});
		addComponent(button);

	}

	private void addObject() {
		boolean validated = true;
		for (UiField field : fields) {
			try {
				field.validate();
			} catch (InvalidValueException e) {
				validated = false;
			}
		}
		if (validated) {
			dbProvider.getObjectsProvider().addNewObject(fields,
					photoContainer.serializePhotos(dbProvider.getGridFS()));
			Notification.show("Zapisano obiekt", Type.HUMANIZED_MESSAGE);
			saveAction.run();
			for (UiField field : fields) {
				field.clear();
				photoContainer.clear();
			}
		} else {
			Notification.show("Uzupełnij wszystkie wymagane pola.", Type.ERROR_MESSAGE);
		}
	}

}

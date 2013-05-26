package pl.art.mnp.rogalin.ui.tab;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.field.FieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;
import pl.art.mnp.rogalin.ui.photo.PhotoContainer;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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

	private final Map<FieldInfo, UiFieldType> fields = new LinkedHashMap<FieldInfo, UiFieldType>();

	private final Map<FieldInfo, UiFieldType> dependentFields = new HashMap<FieldInfo, UiFieldType>();

	private final PhotoContainer photoContainer;

	private final SaveActionListener saveAction;

	private final DBObject editedObject;

	public ObjectForm(SaveActionListener saveAction) {
		this(saveAction, null);
	}

	public ObjectForm(SaveActionListener saveAction, DBObject object) {
		super();
		this.editedObject = object;
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
			FieldType fieldType = f.getFieldType();
			UiFieldType formField = fieldType.getFormField();
			formField.setFromDbObject(object);
			fields.put(f, formField);
			if (f.getDependsOn() != null) {
				FieldInfo dependsOn = f.getDependsOn();
				dependentFields.put(dependsOn, formField);
				boolean visible = false;
				if (object != null) {
					visible = f.isVisible(object.get(dependsOn.name()));
				}
				formField.setEnabled(visible);
			}

			Layout container;
			if (f.isBelowColumns()) {
				container = belowColumns;
			} else if (i <= 22) {
				container = leftColumn;
				i++;
			} else {
				container = rightColumn;
			}
			container.addComponent(formField.getComponent());
		}
		setDependencies();

		photoContainer = new PhotoContainer(object);
		addComponent(photoContainer);

		Button button = new Button("Zapisz obiekt");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				saveObject();
			}
		});
		addComponent(button);
	}

	private void setDependencies() {
		for (final Entry<FieldInfo, UiFieldType> entry : fields.entrySet()) {
			if (dependentFields.containsKey(entry.getKey())) {
				entry.getValue().addOnChangeListener(new ValueChangeListener() {
					@Override
					public void valueChange(ValueChangeEvent event) {
						UiFieldType dependentField = dependentFields.get(entry.getKey());
						boolean visible = entry.getKey().isVisible(event.getProperty().getValue());
						dependentField.setEnabled(visible);
					}
				});
			}
		}
	}

	private void saveObject() {
		for (Entry<FieldInfo, UiFieldType> entry : fields.entrySet()) {
			try {
				entry.getValue().validate();
			} catch (InvalidValueException e) {
				Notification.show(String.format("%s: %s", entry.getKey().toString(), e.getMessage()),
						Type.ERROR_MESSAGE);
				return;
			}
		}
		BasicDBList photos = photoContainer.serializePhotos();
		ObjectsDao dao = DbConnection.getInstance().getObjectsDao();
		if (editedObject == null) {
			dao.addObject(fields, photos);
		} else {
			dao.updateObject(fields, photos, (ObjectId) editedObject.get("_id"));
		}
		Notification.show("Zapisano obiekt", Type.HUMANIZED_MESSAGE);
		saveAction.onSaveAction();
		for (UiFieldType field : fields.values()) {
			field.clear();
			photoContainer.clear();
		}
	}
}

package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

public class ObjectForm extends VerticalLayout {

	private static final long serialVersionUID = 4289893965280993484L;

	private final Map<FieldInfo, UiFieldType> fields = new LinkedHashMap<FieldInfo, UiFieldType>();

	private final Map<FieldInfo, List<FieldInfo>> dependentFields = new HashMap<FieldInfo, List<FieldInfo>>();

	private final PhotoContainer photoContainer;

	private final SaveActionListener saveAction;

	private final DBObject editedObject;

	public ObjectForm(SaveActionListener saveAction) {
		this(saveAction, null);
	}

	@SuppressWarnings("serial")
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
				if (!dependentFields.containsKey(dependsOn)) {
					dependentFields.put(dependsOn, new ArrayList<FieldInfo>());
				}
				dependentFields.get(dependsOn).add(f);
				boolean visible = false;
				if (object != null) {
					visible = f.isVisible(object.get(dependsOn.name()));
				}
				formField.setEnabled(visible);
			}

			Layout container;
			if (f.isBelowColumns()) {
				container = belowColumns;
			} else if (i <= 21) {
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
			final FieldInfo fieldInfo = entry.getKey();
			final UiFieldType uiFieldType = entry.getValue();
			if (dependentFields.containsKey(fieldInfo)) {
				List<FieldInfo> dependentFieldList = dependentFields.get(fieldInfo);
				for (final FieldInfo dependentFieldInfo : dependentFieldList) {
					final UiFieldType dependentField = fields.get(dependentFieldInfo);
					uiFieldType.addOnChangeListener(new ValueChangeListener() {
						private static final long serialVersionUID = -7267921116400453444L;

						@Override
						public void valueChange(ValueChangeEvent event) {
							boolean visible = dependentFieldInfo.isVisible(uiFieldType.getDbObject());
							dependentField.setEnabled(visible);
						}
					});
				}
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

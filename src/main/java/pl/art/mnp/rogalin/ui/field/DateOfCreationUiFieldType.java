package pl.art.mnp.rogalin.ui.field;

import java.util.Arrays;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.field.creationdate.DateType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DateOfCreationUiFieldType extends AbstractUiFieldType {

	private static final Component EMPTY_COMPONENT = new Label("");

	private final VerticalLayout verticalLayout;

	private final CheckBox notSure;

	private final ComboBox dateTypeCombo;

	private Component subComponent;

	private UiFieldType subField;

	public DateOfCreationUiFieldType(final FieldInfo fieldInfo) {
		super(fieldInfo);
		verticalLayout = new VerticalLayout();
		verticalLayout.setCaption(fieldInfo.toString());

		dateTypeCombo = new ComboBox(null, Arrays.asList(DateType.values()));
		dateTypeCombo.setNullSelectionAllowed(false);
		dateTypeCombo.setNewItemsAllowed(false);
		dateTypeCombo.setTextInputAllowed(false);
		dateTypeCombo.select(DateType.NONE);
		dateTypeCombo.setImmediate(true);
		dateTypeCombo.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				DateType dateType = (DateType) event.getProperty().getValue();
				subField = dateType.getUiFieldType(fieldInfo);
				Component previousComponent = subComponent;
				if (subField == null) {
					subComponent = EMPTY_COMPONENT;
					notSure.setVisible(false);
				} else {
					subComponent = subField.getComponent();
					notSure.setVisible(true);
				}
				verticalLayout.replaceComponent(previousComponent, subComponent);
			}
		});
		verticalLayout.addComponent(dateTypeCombo);

		subComponent = EMPTY_COMPONENT;
		verticalLayout.addComponent(subComponent);

		notSure = new CheckBox("Datowanie niepewne");
		notSure.setVisible(false);
		verticalLayout.addComponent(notSure);
	}

	@Override
	public Component getComponent() {
		return verticalLayout;
	}

	@Override
	public void validate() {
		dateTypeCombo.validate();
		if (subField != null) {
			subField.validate();
		}
	}

	@Override
	public void clear() {
		dateTypeCombo.select(DateType.NONE);
		subComponent = EMPTY_COMPONENT;
		if (subField != null) {
			subField.clear();
			subField = null;
		}
		notSure.setValue(false);
		notSure.setVisible(false);
	}

	@Override
	public Object getDbObject() {
		if (subComponent == null) {
			return null;
		}
		DBObject obj = new BasicDBObject();
		DateType dateType = ((DateType) dateTypeCombo.getValue());
		obj.put("dateType", dateType.name());
		if (dateType == DateType.NONE) {
			return obj;
		}
		obj.put("date", subField.getDbObject());
		obj.put("notSure", notSure.getValue());
		return obj;
	}

	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		DBObject value = (DBObject) object.get(fieldInfo.name());
		if (value == null) {
			return;
		}
		DateType dateType = DateType.valueOf((String) value.get("dateType"));
		dateTypeCombo.select(dateType);
		if (dateType == DateType.NONE) {
			return;
		}
		subField.setFromDbObject(value);
		notSure.setValue((Boolean) value.get("notSure"));
	}
}

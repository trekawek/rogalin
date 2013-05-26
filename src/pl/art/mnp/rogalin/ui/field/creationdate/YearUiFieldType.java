package pl.art.mnp.rogalin.ui.field.creationdate;

import com.mongodb.DBObject;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.AbstractUiFieldType;

@SuppressWarnings("serial")
public class YearUiFieldType extends AbstractUiFieldType {

	private final TextField year;

	public YearUiFieldType(FieldInfo fieldInfo) {
		super(fieldInfo);
		year = new TextField();
		year.setWidth("40px");
		year.setRequired(true);
		year.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public Component getComponent() {
		return year;
	}

	@Override
	public Object getDbObject() {
		return year.getValue();
	}

	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		String value = (String) object.get("date");
		if (value == null) {
			return;
		}
		year.setValue(value);
	}

	@Override
	public void validate() {
		super.validate();
		try {
			Integer.valueOf(year.getValue());
		} catch(NumberFormatException e) {
			throw new com.vaadin.data.Validator.InvalidValueException("to nie jest poprawny rok");
		}
	}
}

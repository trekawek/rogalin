package pl.art.mnp.rogalin.ui.field.creationdate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.AbstractUiFieldType;

@SuppressWarnings("serial")
public class YearRangeUiFieldType extends AbstractUiFieldType {

	private final HorizontalLayout layout;

	private final TextField fromYear;

	private final TextField toYear;

	public YearRangeUiFieldType(FieldInfo fieldInfo) {
		super(fieldInfo);
		layout = new HorizontalLayout();
		fromYear = new TextField();
		fromYear.setWidth("40px");
		fromYear.setRequired(true);
		fromYear.setRequiredError(EMPTY_FIELD_ERROR);
		toYear = new TextField();
		toYear.setWidth("40px");
		toYear.setRequired(true);
		toYear.setRequiredError(EMPTY_FIELD_ERROR);
		layout.addComponents(fromYear, new Label("-"), toYear);
	}

	@Override
	public Component getComponent() {
		return layout;
	}

	@Override
	public Object getDbObject() {
		DBObject o = new BasicDBObject();
		o.put("from", fromYear.getValue());
		o.put("to", toYear.getValue());
		return o;
	}

	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		DBObject value = (DBObject) object.get("date");
		if (value == null) {
			return;
		}
		fromYear.setValue((String) value.get("from"));
		toYear.setValue((String) value.get("to"));
	}

	@Override
	public void validate() {
		fromYear.validate();
		toYear.validate();
		try {
			Integer.valueOf(fromYear.getValue());
			Integer.valueOf(toYear.getValue());
		} catch (NumberFormatException e) {
			throw new com.vaadin.data.Validator.InvalidValueException("to nie jest poprawny rok");
		}
	}

	@Override
	public void clear() {
		fromYear.setValue("");
		toYear.setValue("");
	}

}

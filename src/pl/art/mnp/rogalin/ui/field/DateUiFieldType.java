package pl.art.mnp.rogalin.ui.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.EqualsPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.mongodb.DBObject;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class DateUiFieldType extends AbstractUiFieldType {

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

	private final DateField dateField;
	
	private final boolean search;

	public DateUiFieldType(FieldInfo field, boolean search) {
		super(field);
		this.search = search;
		dateField = new DateField();
		dateField.setCaption(field.toString());
		if (!search) {
			dateField.setValue(new Date());
			dateField.setRequired(field.isRequired());
		}
		dateField.setDateFormat(DATE_FORMAT);
		dateField.setValidationVisible(true);
		dateField.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public Object getDbObject() {
		return df.format(dateField.getValue());
	}

	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		String value = (String) object.get(fieldInfo.name());
		if (value == null) {
			return;
		}
		try {
			dateField.setValue(df.parse(value));
		} catch (ParseException e) {
			LOG.log(Level.SEVERE, "Can't parse date", e);
		}
	}

	@Override
	public void clear() {
		if (search) {
			dateField.setValue(null);
		} else {
			dateField.setValue(new Date());
		}
	}

	@Override
	public Component getComponent() {
		return dateField;
	}

	@Override
	public Predicate getPredicate() {
		if (dateField.getValue() == null) {
			return DUMMY_PREDICATE;
		}
		return new EqualsPredicate(df.format(dateField.getValue()), fieldInfo.name());
	}
}

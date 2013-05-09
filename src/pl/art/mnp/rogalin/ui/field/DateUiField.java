package pl.art.mnp.rogalin.ui.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.DBObject;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class DateUiField extends AbstractUiField {

	private static final DateFormat DF = new SimpleDateFormat("yyy-MM-dd");

	private final DateField dateField;

	public DateUiField(FieldInfo field) {
		super(field);
		dateField = new DateField();
		dateField.setCaption(field.toString());
		dateField.setValue(new Date());
		dateField.setDateFormat("yyyy-MM-dd");
		dateField.setRequired(field.isRequired());
		dateField.setValidationVisible(true);
		dateField.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public DateField getComponent() {
		return dateField;
	}

	@Override
	public Object serializeToMongo() {
		return DF.format(dateField.getValue());
	}

	@Override
	public void deserializeFromMongo(DBObject object) {
		if (object == null) {
			return;
		}
		String value = (String) object.get(field.name());
		if (value == null) {
			return;
		}
		try {
			dateField.setValue(DF.parse(value));
		} catch (ParseException e) {
			LOG.log(Level.SEVERE, "Can't parse date", e);
		}
	}
}

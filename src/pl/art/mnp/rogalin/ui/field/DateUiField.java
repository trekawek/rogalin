package pl.art.mnp.rogalin.ui.field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.art.mnp.rogalin.model.Field;

import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class DateUiField extends AbstractUiField {

	private static final DateFormat DF = new SimpleDateFormat("yyy-MM-dd");

	private final DateField dateField;

	public DateUiField(Field field) {
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
}

package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.DateUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class DateFieldType extends AbstractFieldType {
	public DateFieldType(FieldInfo field) {
		super(field);
	}

	@Override
	public UiFieldType getFormField() {
		return new DateUiFieldType(field, false);
	}

	@Override
	public UiFieldType getSearchField() {
		return new DateUiFieldType(field, true);
	}

}

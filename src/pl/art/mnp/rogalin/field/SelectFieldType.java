package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.SelectUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class SelectFieldType extends AbstractFieldType {

	public SelectFieldType(FieldInfo field) {
		super(field);
	}

	@Override
	public boolean hasOptions() {
		return true;
	}

	@Override
	public UiFieldType getFormField() {
		return new SelectUiFieldType(field, getOptions(), false);
	}

	@Override
	public UiFieldType getSearchField() {
		return new SelectUiFieldType(field, getOptions(), true);
	}
}

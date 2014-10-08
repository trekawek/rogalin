package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.SelectUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class SelectFieldType extends AbstractFieldType {

	private final boolean allowEmpty;

	public SelectFieldType(FieldInfo field, boolean allowEmpty) {
		super(field);
		this.allowEmpty = allowEmpty;
	}

	@Override
	public boolean hasOptions() {
		return true;
	}

	@Override
	public UiFieldType getFormField() {
		return new SelectUiFieldType(field, getOptions(), false, false, allowEmpty);
	}

	@Override
	public UiFieldType getSearchField() {
		return new SelectUiFieldType(field, getOptions(), true, false, allowEmpty);
	}
}

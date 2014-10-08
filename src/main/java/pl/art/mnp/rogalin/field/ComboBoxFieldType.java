package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.ComboBoxUiFieldType;
import pl.art.mnp.rogalin.ui.field.SelectUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class ComboBoxFieldType extends AbstractFieldType {

	private boolean selectFirstItem;

	public ComboBoxFieldType(final FieldInfo field) {
		super(field);
	}

	public void setSelectFirstItem(boolean selectFirstItem) {
		this.selectFirstItem = selectFirstItem;
	}

	@Override
	public boolean hasOptions() {
		return true;
	}

	@Override
	public UiFieldType getFormField() {
		return new ComboBoxUiFieldType(field, getOptions(), false);
	}

	@Override
	public UiFieldType getSearchField() {
		return new SelectUiFieldType(field, getOptions(), true, selectFirstItem, false);
	}
}

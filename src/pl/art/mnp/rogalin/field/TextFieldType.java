package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.TextUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class TextFieldType extends AbstractFieldType {

	private final boolean disabled;

	public TextFieldType(FieldInfo field) {
		this(field, false);
	}

	public TextFieldType(FieldInfo field, boolean disabled) {
		super(field);
		this.disabled = disabled;
	}

	@Override
	public UiFieldType getFormField() {
		UiFieldType f = new TextUiFieldType(field, false);
		if (disabled) {
			f.getComponent().setEnabled(false);
		}
		return f;
	}

	@Override
	public UiFieldType getSearchField() {
		UiFieldType f = new TextUiFieldType(field, true);
		return f;
	}
}

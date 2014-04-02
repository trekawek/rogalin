package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.TextUiFieldType;
import pl.art.mnp.rogalin.ui.field.TextWithDefaultUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

@SuppressWarnings("serial")
public class TextFieldType extends AbstractFieldType {

	private final boolean disabled;

	private final String defaultValue;

	public TextFieldType(FieldInfo field) {
		this(field, null, false);
	}

	public TextFieldType(FieldInfo field, String defaultValue, boolean disabled) {
		super(field);
		this.disabled = disabled;
		this.defaultValue = defaultValue;
	}

	@Override
	public UiFieldType getFormField() {
		UiFieldType f = getTextUiFieldType(false);
		if (disabled) {
			f.getComponent().setEnabled(false);
		}
		return f;
	}

	@Override
	public UiFieldType getSearchField() {
		return getTextUiFieldType(true);
	}

	private UiFieldType getTextUiFieldType(boolean search) {
		UiFieldType f;
		if (defaultValue == null) {
			f = new TextUiFieldType(field, search);
		} else {
			f = new TextWithDefaultUiFieldType(field, search, defaultValue);
		}
		return f;
	}
}

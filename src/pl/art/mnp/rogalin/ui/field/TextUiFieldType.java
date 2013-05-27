package pl.art.mnp.rogalin.ui.field;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.ContainsPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextUiFieldType extends AbstractUiFieldType {

	private final TextField textField;

	private final String defaultValue;

	public TextUiFieldType(FieldInfo field, boolean search) {
		this(field, search, null);
	}

	public TextUiFieldType(FieldInfo field, boolean search, String defaultValue) {
		super(field);
		this.defaultValue = StringUtils.defaultString(defaultValue);
		textField = new TextField();
		textField.setCaption(field.toString());
		textField.setValue(this.defaultValue);
		textField.setValidationVisible(true);
		if (!search) {
			textField.setRequired(field.isRequired());
		}
		textField.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public void clear() {
		textField.setValue(defaultValue);
	}

	@Override
	public TextField getComponent() {
		return textField;
	}

	@Override
	public Predicate getPredicate() {
		if (StringUtils.isEmpty(textField.getValue())) {
			return DUMMY_PREDICATE;
		}
		return new ContainsPredicate(textField.getValue(), fieldInfo.name());
	}
}

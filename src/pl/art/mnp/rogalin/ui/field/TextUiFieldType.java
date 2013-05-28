package pl.art.mnp.rogalin.ui.field;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.ContainsPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextUiFieldType extends AbstractUiFieldType {

	private final TextField textField;

	public TextUiFieldType(FieldInfo field, boolean search) {
		super(field);
		textField = new TextField();
		textField.setCaption(field.toString());
		textField.setValidationVisible(true);
		if (!search) {
			textField.setRequired(field.isRequired());
		}
		textField.setRequiredError(EMPTY_FIELD_ERROR);
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

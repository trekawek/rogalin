package pl.art.mnp.rogalin.ui.field;

import pl.art.mnp.rogalin.model.Field;

import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextUiField extends AbstractUiField {

	private final TextField textField;

	public TextUiField(Field field) {
		super(field);
		textField = new TextField();
		textField.setCaption(field.toString());
		textField.setRequired(field.isRequired());
		textField.setValidationVisible(true);
		textField.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public TextField getComponent() {
		return textField;
	}

	@Override
	public void clear() {
		textField.setValue("");
	}
}

package pl.art.mnp.rogalin.ui.field;

import pl.art.mnp.rogalin.model.Field;

import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class TextAreaUiField extends AbstractUiField {

	private final TextArea textArea;

	public TextAreaUiField(Field field) {
		super(field);
		textArea = new TextArea();
		textArea.setCaption(field.toString());
		textArea.setRequired(field.isRequired());
		textArea.setValidationVisible(true);
		textArea.setRequiredError(EMPTY_FIELD_ERROR);
		field.setComponentProperties(textArea);
	}

	@Override
	public TextArea getComponent() {
		return textArea;
	}
}

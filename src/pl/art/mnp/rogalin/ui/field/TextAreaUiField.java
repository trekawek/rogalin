package pl.art.mnp.rogalin.ui.field;

import pl.art.mnp.rogalin.model.FieldInfo;

import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class TextAreaUiField extends AbstractUiField {

	private final TextArea textArea;

	public TextAreaUiField(FieldInfo field) {
		super(field);
		textArea = new TextArea();
		textArea.setCaption(field.toString());
		textArea.setRequired(field.isRequired());
		textArea.setValidationVisible(true);
		textArea.setRequiredError(EMPTY_FIELD_ERROR);
		if (field == FieldInfo.DESC) {
			textArea.setColumns(40);
			textArea.setRows(6);
		}
		field.setComponentProperties(textArea);
	}

	@Override
	public TextArea getComponent() {
		return textArea;
	}

	@Override
	public void clear() {
		textArea.setValue("");
	}
}

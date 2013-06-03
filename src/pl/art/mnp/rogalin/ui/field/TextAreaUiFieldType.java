package pl.art.mnp.rogalin.ui.field;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.ContainsPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class TextAreaUiFieldType extends AbstractUiFieldType {

	private final TextArea textArea;

	public TextAreaUiFieldType(FieldInfo field, boolean search) {
		super(field);
		textArea = new TextArea();
		textArea.setCaption(field.toString());
		textArea.setValidationVisible(true);
		if (!search) {
			textArea.setRequired(field.isRequired());
		}
		textArea.setRequiredError(EMPTY_FIELD_ERROR);
		textArea.setColumns(20);
		if (field == FieldInfo.DESC) {
			textArea.setColumns(40);
			textArea.setRows(6);
		}
	}

	@Override
	public TextArea getComponent() {
		return textArea;
	}

	@Override
	public void clear() {
		textArea.setValue("");
	}

	@Override
	public Predicate getPredicate() {
		if (StringUtils.isEmpty(textArea.getValue())) {
			return DUMMY_PREDICATE;
		}
		return new ContainsPredicate(textArea.getValue(), fieldInfo.name());
	}
}

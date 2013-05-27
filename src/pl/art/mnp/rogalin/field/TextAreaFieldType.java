package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.TextAreaUiFieldType;
import pl.art.mnp.rogalin.ui.field.TextUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

import com.mongodb.DBObject;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class TextAreaFieldType extends AbstractFieldType {

	public TextAreaFieldType(FieldInfo field) {
		super(field);
	}

	@Override
	public Label getPreviewField(DBObject o) {
		Label label = super.getPreviewField(o);
		label.setContentMode(ContentMode.PREFORMATTED);
		if (field == FieldInfo.DESC) {
			label.setStyleName("desc");
		}
		return label;
	}

	@Override
	public UiFieldType getFormField() {
		return new TextAreaUiFieldType(field, false);
	}

	@Override
	public UiFieldType getSearchField() {
		return new TextUiFieldType(field, true);
	}
}

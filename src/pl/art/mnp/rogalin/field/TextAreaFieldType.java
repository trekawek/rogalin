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
		Label label;
		if (field == FieldInfo.DESC) {
			label = super.getPreviewField(o);
			label.setContentMode(ContentMode.PREFORMATTED);
			label.setStyleName("desc");
		} else {
			label = new Label();
			label.setCaption(getFieldInfo().toString());
			label.setValue(getValue(o).replace("\n", "<br/>"));
			label.setContentMode(ContentMode.HTML);
			label.setStyleName("textArea");
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

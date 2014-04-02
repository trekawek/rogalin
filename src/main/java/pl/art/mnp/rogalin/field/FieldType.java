package pl.art.mnp.rogalin.field;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

import com.mongodb.DBObject;
import com.vaadin.ui.Component;

public interface FieldType {

	FieldInfo getFieldInfo();

	public String getValue(DBObject dbObject);

	boolean hasOptions();

	Component getPreviewField(DBObject o);

	UiFieldType getFormField();

	UiFieldType getSearchField();

}
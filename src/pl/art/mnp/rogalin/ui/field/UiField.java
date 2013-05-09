package pl.art.mnp.rogalin.ui.field;

import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.DBObject;
import com.vaadin.ui.Component;

public interface UiField {
	static final String EMPTY_FIELD_ERROR = "Pole nie może być puste";

	FieldInfo getFieldInfo();

	Component getComponent();

	Object serializeToMongo();

	void validate();

	String getStringValue(DBObject o);

	void clear();

	void deserializeFromMongo(DBObject object);
}
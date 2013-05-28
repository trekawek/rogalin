package pl.art.mnp.rogalin.ui.field;

import java.io.Serializable;

import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.mongodb.DBObject;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;

public interface UiFieldType extends Serializable {

	Component getComponent();

	Object getDbObject();

	void setFromDbObject(DBObject object);

	void validate();

	void clear();

	void addOnChangeListener(ValueChangeListener listener);

	void setEnabled(boolean visible);

	Predicate getPredicate();

}

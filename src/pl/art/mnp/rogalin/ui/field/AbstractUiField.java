package pl.art.mnp.rogalin.ui.field;

import java.io.Serializable;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.Field;

import com.mongodb.DBObject;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class AbstractUiField implements UiField, Serializable {

	protected final Field field;

	protected final MongoDbProvider dbProvider;

	protected AbstractUiField(Field field, MongoDbProvider dbProvider) {
		this.field = field;
		this.dbProvider = dbProvider;
	}

	protected AbstractUiField(Field field) {
		this(field, null);
	}

	@Override
	public Field getFieldInfo() {
		return field;
	}

	@Override
	public Object serializeToMongo() {
		Component component = getComponent();
		if (component instanceof AbstractField) {
			@SuppressWarnings("rawtypes")
			AbstractField field = (AbstractField) component;
			return field.getValue();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void validate() {
		Component component = getComponent();
		if (component instanceof AbstractField) {
			@SuppressWarnings("rawtypes")
			AbstractField field = (AbstractField) component;
			field.validate();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String getStringValue(DBObject o) {
		Object value = o.get(getFieldInfo().name());
		if (value == null) {
			value = "-";
		}
		return value.toString();
	}
}

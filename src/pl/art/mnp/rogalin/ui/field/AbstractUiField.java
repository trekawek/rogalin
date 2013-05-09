package pl.art.mnp.rogalin.ui.field;

import java.io.Serializable;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.DBObject;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class AbstractUiField implements UiField, Serializable {

	protected static final Logger LOG = Logger.getLogger(AbstractUiField.class.getName());

	protected final FieldInfo field;

	protected final MongoDbProvider dbProvider;

	protected AbstractUiField(FieldInfo field, MongoDbProvider dbProvider) {
		this.field = field;
		this.dbProvider = dbProvider;
	}

	protected AbstractUiField(FieldInfo field) {
		this(field, null);
	}

	@Override
	public FieldInfo getFieldInfo() {
		return field;
	}

	@SuppressWarnings("rawtypes")
	private AbstractField getAbstractField() {
		Component component = getComponent();
		if (component instanceof AbstractField) {
			return (AbstractField) component;
		} else {
			throw new UnsupportedOperationException();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		getAbstractField().setValue(null);
	}

	@Override
	public Object serializeToMongo() {
		return getAbstractField().getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeFromMongo(DBObject object) {
		if (object == null) {
			return;
		}
		Object value = object.get(field.name());
		if (value == null) {
			return;
		}
		getAbstractField().setValue(value);
	}

	@Override
	public void validate() {
		getAbstractField().validate();
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

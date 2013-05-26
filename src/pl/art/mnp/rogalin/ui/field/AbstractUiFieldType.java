package pl.art.mnp.rogalin.ui.field;

import java.util.logging.Logger;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.DummyPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.mongodb.DBObject;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public abstract class AbstractUiFieldType implements UiFieldType {
	protected static final Logger LOG = Logger.getLogger(AbstractUiFieldType.class.getName());

	protected static final String EMPTY_FIELD_ERROR = "pole nie może być puste";

	protected static final Predicate DUMMY_PREDICATE = new DummyPredicate();

	protected final FieldInfo fieldInfo;

	public AbstractUiFieldType(FieldInfo fieldInfo) {
		this.fieldInfo = fieldInfo;
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
	public Object getDbObject() {
		return getAbstractField().getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		Object value = object.get(fieldInfo.name());
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
	public void addOnChangeListener(ValueChangeListener listener) {
		getAbstractField().addValueChangeListener(listener);
	}

	@Override
	public void setEnabled(boolean visible) {
		getAbstractField().setVisible(visible);
	}

	@Override
	public abstract Component getComponent();

	@Override
	public Predicate getPredicate() {
		return DUMMY_PREDICATE;
	}
}

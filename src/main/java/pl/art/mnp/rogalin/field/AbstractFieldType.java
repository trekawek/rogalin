package pl.art.mnp.rogalin.field;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

import com.mongodb.DBObject;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public abstract class AbstractFieldType implements FieldType, Serializable {

	protected static final Logger LOG = Logger.getLogger(AbstractFieldType.class.getName());

	protected final FieldInfo field;

	protected AbstractFieldType(FieldInfo field) {
		this.field = field;
	}

	@Override
	public FieldInfo getFieldInfo() {
		return field;
	}

	@Override
	public String getValue(DBObject dbObject) {
		Object value = dbObject.get(field.name());
		if (value == null) {
			value = "";
		}
		return value.toString();
	}

	@Override
	public Label getPreviewField(DBObject o) {
		Label field = new Label();
		field.setCaption(getFieldInfo().toString());
		field.setValue(getValue(o));
		return field;
	}

	protected List<String> getOptions() {
		List<String> options;
		options = DbConnection.getInstance().getOptionsDao().getOptions(field);
		return options;
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public UiFieldType getSearchField() {
		return getFormField();
	}
}

package pl.art.mnp.rogalin.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.MultiSelectUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;

import com.mongodb.DBObject;

public class MultiSelectFieldType extends AbstractFieldType {

	private static final long serialVersionUID = 8896033855511526689L;

	private final boolean showOther;

	public MultiSelectFieldType(FieldInfo field, boolean showOther) {
		super(field);
		this.showOther = showOther;
	}

	@Override
	public boolean hasOptions() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getValue(DBObject dbObject) {
		DBObject compositeValue = (DBObject) dbObject.get(field.name());
		List<String> values = new ArrayList<String>();
		if (compositeValue != null) {
			if (compositeValue.containsField("values")) {
				values.addAll((List<String>) compositeValue.get("values"));
			}
			String other = (String) compositeValue.get("other");
			if (other != null) {
				values.add(other);
			}
		}
		return StringUtils.join(values, ", ");
	}

	@Override
	public UiFieldType getFormField() {
		return new MultiSelectUiFieldType(field, getOptions(), false, showOther);
	}

	@Override
	public UiFieldType getSearchField() {
		return new MultiSelectUiFieldType(field, getOptions(), true, false);
	}

}

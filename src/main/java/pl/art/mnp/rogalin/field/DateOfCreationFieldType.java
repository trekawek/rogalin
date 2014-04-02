package pl.art.mnp.rogalin.field;

import com.mongodb.DBObject;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.field.creationdate.DateType;
import pl.art.mnp.rogalin.field.creationdate.Range;
import pl.art.mnp.rogalin.ui.field.DateOfCreationUiFieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;
import pl.art.mnp.rogalin.ui.field.creationdate.CenturyUiFieldType;

@SuppressWarnings("serial")
public class DateOfCreationFieldType extends AbstractFieldType {

	public DateOfCreationFieldType(FieldInfo field) {
		super(field);
	}

	@Override
	public UiFieldType getFormField() {
		return new DateOfCreationUiFieldType(field);
	}

	@Override
	public String getValue(DBObject dbObject) {
		DBObject value = (DBObject) dbObject.get(field.name());
		if (value == null) {
			return "";
		}
		DateType dateType = DateType.valueOf((String) value.get("dateType"));
		if (dateType == DateType.NONE) {
			return "";
		}
		String date = dateType.getStringValue(value.get("date"));
		String notSure = "";
		if (Boolean.TRUE.equals(value.get("notSure"))) {
			notSure = " (?)";
		}
		return date + notSure;
	}

	public Range getRange(DBObject dbObject) {
		DBObject value = (DBObject) dbObject.get(field.name());
		if (value == null) {
			return Range.EMPTY_RANGE;
		}
		DateType dateType = DateType.valueOf((String) value.get("dateType"));
		if (dateType == DateType.NONE) {
			return Range.EMPTY_RANGE;
		}
		return dateType.getRange(value.get("date"));
	}

	@Override
	public UiFieldType getSearchField() {
		return new CenturyUiFieldType(field, true);
	}
}

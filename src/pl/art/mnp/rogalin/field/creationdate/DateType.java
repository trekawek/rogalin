package pl.art.mnp.rogalin.field.creationdate;

import com.mongodb.DBObject;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.field.UiFieldType;
import pl.art.mnp.rogalin.ui.field.creationdate.CenturyUiFieldType;
import pl.art.mnp.rogalin.ui.field.creationdate.YearRangeUiFieldType;
import pl.art.mnp.rogalin.ui.field.creationdate.YearUiFieldType;

public enum DateType {
	NONE("-") {
		@Override
		public UiFieldType getUiFieldType(FieldInfo fieldInfo) {
			return null;
		}

		@Override
		public String getStringValue(Object object) {
			return "";
		}

		@Override
		public Range getRange(Object object) {
			return Range.EMPTY_RANGE;
		}
	},
	YEAR("Rok") {
		@Override
		public UiFieldType getUiFieldType(FieldInfo fieldInfo) {
			return new YearUiFieldType(fieldInfo);
		}

		@Override
		public String getStringValue(Object object) {
			return (String) object;
		}

		@Override
		public Range getRange(Object object) {
			return new Range(Integer.valueOf((String) object));
		}
	},
	YEAR_RANGE("Zakres lat") {
		@Override
		public UiFieldType getUiFieldType(FieldInfo fieldInfo) {
			return new YearRangeUiFieldType(fieldInfo);
		}

		@Override
		public String getStringValue(Object object) {
			DBObject dbObject = (DBObject) object;
			return String.format("%s - %s", dbObject.get("from"), dbObject.get("to"));
		}

		@Override
		public Range getRange(Object object) {
			DBObject dbObject = (DBObject) object;
			Integer from = Integer.valueOf((String) dbObject.get("from"));
			Integer to = Integer.valueOf((String) dbObject.get("to"));
			return new Range(from, to);
		}
	},
	CENTURY("Wiek") {
		@Override
		public UiFieldType getUiFieldType(FieldInfo fieldInfo) {
			return new CenturyUiFieldType(fieldInfo, false);
		}

		@Override
		public String getStringValue(Object object) {
			DBObject dbObject = (DBObject) object;
			CenturyType centuryType = CenturyType.valueOf((String) dbObject.get("centuryType"));
			Century century = new Century((Integer) dbObject.get("century"));
			return String.format("%s %s w.", centuryType, century);
		}

		@Override
		public Range getRange(Object object) {
			DBObject dbObject = (DBObject) object;
			CenturyType centuryType = CenturyType.valueOf((String) dbObject.get("centuryType"));
			Century century = new Century((Integer) dbObject.get("century"));
			return centuryType.getRange().add((century.getCentury() - 1) * 100);
		}
	};

	private final String label;

	private DateType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	public abstract UiFieldType getUiFieldType(FieldInfo fieldInfo);

	public abstract String getStringValue(Object object);

	public abstract Range getRange(Object object);
}
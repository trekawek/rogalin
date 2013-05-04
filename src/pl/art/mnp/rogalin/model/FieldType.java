package pl.art.mnp.rogalin.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mongodb.DBObject;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.ui.field.ComboBoxUiField;
import pl.art.mnp.rogalin.ui.field.DateUiField;
import pl.art.mnp.rogalin.ui.field.MultiSelectUiField;
import pl.art.mnp.rogalin.ui.field.SelectUiField;
import pl.art.mnp.rogalin.ui.field.TextAreaUiField;
import pl.art.mnp.rogalin.ui.field.TextUiField;
import pl.art.mnp.rogalin.ui.field.UiField;

public enum FieldType {
	TEXT() {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new TextUiField(field);
		}
	},
	TEXT_AREA() {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new TextAreaUiField(field);
		}
	},
	SELECT(true) {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new SelectUiField(field, dbProvider);
		}
	},
	MULTI_SELECT(true) {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new MultiSelectUiField(field, dbProvider);
		}

		@SuppressWarnings("unchecked")
		@Override
		String getValue(DBObject dbObject, Field f) {
			DBObject compositeValue = (DBObject) dbObject.get(f.name());
			List<String> values = new ArrayList<String>();
			values.addAll((List<String>) compositeValue.get("values"));
			String other = (String) compositeValue.get("other");
			if (other != null) {
				values.add(other);
			}
			return StringUtils.join(values, ", ");
		}
	},
	COMBO(true) {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new ComboBoxUiField(field, dbProvider);
		}
	},
	DATE() {
		@Override
		public UiField getUiField(Field field, MongoDbProvider dbProvider) {
			return new DateUiField(field);
		}
	};

	private final boolean isList;

	public abstract UiField getUiField(Field field, MongoDbProvider dbProvider);

	private FieldType() {
		this(false);
	}

	private FieldType(boolean isList) {
		this.isList = isList;
	}

	public boolean isList() {
		return isList;
	}

	String getValue(DBObject dbObject, Field f) {
		Object value = dbObject.get(f.name());
		if (value == null) {
			value = "";
		}
		return value.toString();
	}
}

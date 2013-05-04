package pl.art.mnp.rogalin.model;

import com.mongodb.DBObject;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.ui.field.UiField;

public enum Field {
//@formatter:off
	IDENTIFIER("Numer inwentarzowy", FieldType.TEXT),
	NAME("Nazwa/Przedmiot/Tytuł", FieldType.TEXT, true),
	AUTHOR("Autor", FieldType.TEXT),
	DATE("Data powstania", FieldType.TEXT),
	TYPE("Dziedzina", FieldType.SELECT, true),
	TECHNIQUE("Technika", FieldType.COMBO, true),
	CARRIER("Nośnik", FieldType.COMBO, true),
	HEIGHT("Wysokość", FieldType.TEXT),
	WIDTH("Szerokość", FieldType.TEXT),
	DEPTH("Głębokość", FieldType.TEXT),
	PART("Część kompletu", FieldType.TEXT),
	DEPARTMENT("Oddział/Dział", FieldType.TEXT, true),
	HOME("Stałe miejsce przechowywania", FieldType.TEXT, true),
	TEMPORARY_HOME("Tymczasowe miejsce przechowywania", FieldType.TEXT, true),
	PARTS_NO("Ilość elementów składowych", FieldType.TEXT),
	PARTS_IN_PACKAGE("Ilość elementów po spakowaniu", FieldType.TEXT),
	CONDITION("Stan obiektu", FieldType.SELECT),
	COMPLETENESS("Kompletność", FieldType.SELECT),
	ELEMENT_ABSENCE("Brak elementu", FieldType.TEXT),
	CONDITION_DESC("Uwagi do kompletności", FieldType.TEXT_AREA),
	EVALUATION_DATE("Data oceny", FieldType.DATE),
	SUPPORT_DAMAGES("Uszkodzenia podobrazia", FieldType.MULTI_SELECT),
	OTHER_DAMAGES("Uszkodzenia warstw dekoracyjnych", FieldType.MULTI_SELECT),
	DESC("Uwagi", FieldType.TEXT_AREA) {
		@Override
		public boolean isBelowColumns() {
			return true;
		}
		
		@Override
		public void setComponentProperties(Component c) {
			TextArea t= (TextArea)c;
			t.setColumns(40);
			t.setRows(6);
		}
	};
//@formatter:on

	private final String label;

	private final FieldType type;

	private final boolean required;

	private Field(String label, FieldType type, boolean required) {
		this.label = label;
		this.type = type;
		this.required = required;
	}

	private Field(String label, FieldType type) {
		this(label, type, false);
	}

	@Override
	public String toString() {
		return label;
	}

	public UiField getUiField(MongoDbProvider dbProvider) {
		return type.getUiField(this, dbProvider);
	}

	public String getStringValue(DBObject o) {
		return type.getValue(o, this);
	}

	public boolean isRequired() {
		return required;
	}

	public FieldType getFieldType() {
		return type;
	}

	public boolean isBelowColumns() {
		return false;
	}

	public void setComponentProperties(Component c) {
	}
}

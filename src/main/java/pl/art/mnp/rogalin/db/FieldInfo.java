package pl.art.mnp.rogalin.db;

import java.util.Collection;

import com.mongodb.DBObject;

import pl.art.mnp.rogalin.field.DateOfCreationFieldType;
import pl.art.mnp.rogalin.field.ComboBoxFieldType;
import pl.art.mnp.rogalin.field.DateFieldType;
import pl.art.mnp.rogalin.field.MultiSelectFieldType;
import pl.art.mnp.rogalin.field.SelectFieldType;
import pl.art.mnp.rogalin.field.TextAreaFieldType;
import pl.art.mnp.rogalin.field.TextFieldType;
import pl.art.mnp.rogalin.field.FieldType;

public enum FieldInfo {
//@formatter:off
	IDENTIFIER("Numer inwentarzowy MNP", true, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	NAME("Nazwa/Przedmiot/Tytuł", true, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		} 
	},
	AUTHOR("Autor", true, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this, "nieznany", false);
		}
	},
	DATE_OF_CREATION("Data powstania", false, true) {
		@Override
		public FieldType getFieldType() {
			return new DateOfCreationFieldType(this);
		}
	},
	TYPE("Dziedzina", true, true) {
		@Override
		public FieldType getFieldType() {
			return new SelectFieldType(this, false);
		}
	},
	TECHNIQUE("Technika", true, true) {
		@Override
		public FieldType getFieldType() {
			return new MultiSelectFieldType(this, false);
		}
	},
	VENEER_TYPE("Rodzaj forniru", false, true, FieldInfo.TECHNIQUE) {
		@SuppressWarnings("unchecked")
		@Override
		public boolean isVisible(Object technique) {
			DBObject obj = (DBObject) technique;
			Collection<String> coll = (Collection<String>) obj.get("values");
			return coll.contains("fornir");
		}

		@Override
		public FieldType getFieldType() {
			return new MultiSelectFieldType(this, false);
		}
	},
	INTARSIA_TYPE("Rodzaj intarsji", false, true, FieldInfo.TECHNIQUE) {
		@SuppressWarnings("unchecked")
		@Override
		public boolean isVisible(Object technique) {
			DBObject obj = (DBObject) technique;
			Collection<String> coll = (Collection<String>) obj.get("values");
			return coll.contains("intarsja");
		}

		@Override
		public FieldType getFieldType() {
			return new MultiSelectFieldType(this, false);
		}
	},

	CARRIER("Nośnik/Tworzywo", false, true) {
		@Override
		public FieldType getFieldType() {
			return new ComboBoxFieldType(this);
		}
	},
	WOOD_TYPE("Gatunek drewna", false, true, FieldInfo.CARRIER) {
		@Override
		public boolean isVisible(Object carrier) {
			return "drewno".equals(carrier);
		}

		@Override
		public FieldType getFieldType() {
			return new ComboBoxFieldType(this);
		}
	},
	HEIGHT("Wysokość (cm)", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	WIDTH("Szerokość (cm)", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	DEPTH("Głębokość (cm)", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	PART("Część kompletu", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	DEPARTMENT("Oddział/Dział", true, true) {
		@Override
		public FieldType getFieldType() {
			return new ComboBoxFieldType(this);
		}
	},
	HOME("Stałe miejsce przechowywania", true, true) {
		@Override
		public FieldType getFieldType() {
			ComboBoxFieldType fieldType = new ComboBoxFieldType(this);
			fieldType.setSelectFirstItem(true);
			return fieldType;
		}
	},
	LOCATION("Opis lokalizacji", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	/*TEMPORARY_HOME("Tymczasowe miejsce przechowywania", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},*/
	CONTAINER_NO("Nr kontenera", false, true) {
		@Override
		public FieldType getFieldType() {
			return new SelectFieldType(this, true);
		}},
	CONTAINER_SEGMENT("Część kontenera", false, true) {
		@Override
		public FieldType getFieldType() {
			return new SelectFieldType(this, true);
		}},
	PARTS_NO("Ilość elementów składowych", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	PARTS_IN_PACKAGE("Ilość elementów po spakowaniu", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	CONDITION("Stan obiektu", false, true) {
		@Override
		public FieldType getFieldType() {
			return new SelectFieldType(this, false);
		}
	},
	COMPLETENESS("Kompletność", false, true) {
		@Override
		public FieldType getFieldType() {
			return new SelectFieldType(this, false);
		}
	},
	ELEMENT_ABSENCE("Brakujące elementy", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextFieldType(this);
		}
	},
	CONDITION_DESC("Uwagi do kompletności", false, true) {
		@Override
		public FieldType getFieldType() {
			return new TextAreaFieldType(this);
		}
	},
	EVALUATION_DATE("Data oceny", false, true) {
		@Override
		public FieldType getFieldType() {
			return new DateFieldType(this);
		}
	},
	SUPPORT_DAMAGES("Uszkodzenia nośnika/tworzywa", false, true) {
		@Override
		public FieldType getFieldType() {
			return new MultiSelectFieldType(this, true);
		}
	},
	OTHER_DAMAGES("Uszkodzenia warstw dekoracyjnych", false, true) {
		@Override
		public FieldType getFieldType() {
			return new MultiSelectFieldType(this, true);
		}
	},
	DESC("Uwagi", false, true) {
		@Override
		public boolean isBelowColumns() {
			return true;
		}

		@Override
		public FieldType getFieldType() {
			return new TextAreaFieldType(this);
		}
	};
//@formatter:on

	private final String label;

	private final boolean required;

	private final boolean searchable;

	private final FieldInfo dependsOn;

	private FieldInfo(String label, boolean required, boolean searchable, FieldInfo dependsOn) {
		this.label = label;
		this.required = false;
		this.dependsOn = dependsOn;
		this.searchable = searchable;
	}

	private FieldInfo(String label, boolean required, boolean searchable) {
		this(label, required, searchable, null);
	}

	@Override
	public String toString() {
		return label;
	}

	public abstract FieldType getFieldType();

	public boolean isSearchable() {
		return searchable;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isBelowColumns() {
		return false;
	}

	public boolean isVisible(Object object) {
		return true;
	}

	public FieldInfo getDependsOn() {
		return dependsOn;
	}
}

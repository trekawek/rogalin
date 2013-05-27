package pl.art.mnp.rogalin.field.creationdate;

public enum CenturyType {
	NONE("") {
		@Override
		public Range getRange() {
			return new Range(1, 100);
		}
	},
	BEGIN("początek") {
		@Override
		public Range getRange() {
			return new Range(1, 25);
		}
	},
	FIRST_HALF("pierwsza połowa") {
		@Override
		public Range getRange() {
			return new Range(1, 50);
		}
	},
	MID("połowa") {
		@Override
		public Range getRange() {
			return new Range(45, 55);
		}
	},
	SECOND_HALF("druga połowa") {
		@Override
		public Range getRange() {
			return new Range(51, 100);
		}
	},
	END("koniec") {
		@Override
		public Range getRange() {
			return new Range(75, 100);
		}
	},
	TURN("przełom") {
		@Override
		public Range getRange() {
			return new Range(75, 125);
		}
	};

	private final String label;

	private CenturyType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	public abstract Range getRange();

}
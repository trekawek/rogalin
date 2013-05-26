package pl.art.mnp.rogalin.field.creationdate;

public class Century {

	private enum RomanDigit {
		I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
		private int weight;

		private RomanDigit(int weight) {
			this.weight = weight;
		}

		public static int getInt(char c) {
			return RomanDigit.valueOf(String.valueOf(c)).weight;
		}

		public int getInt() {
			return weight;
		}
	};

	private final int century;

	public Century(String century) {
		this.century = decode(century);
	}

	public Century(int century) {
		this.century = century;
	}

	public int getCentury() {
		return century;
	}

	public String toString() {
		return encode(century);
	}

	private static String encode(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException();
		}

		StringBuilder buf = new StringBuilder();
		final RomanDigit[] values = RomanDigit.values();
		for (int i = values.length - 1; i >= 0; i--) {
			while (n >= values[i].getInt()) {
				buf.append(values[i]);
				n -= values[i].getInt();
			}
		}
		return buf.toString();
	}

	private static int decode(String roman) {
		int result = 0;
		String uRoman = roman.toUpperCase();
		for (int i = 0; i < uRoman.length() - 1; i++) {
			if (RomanDigit.getInt(uRoman.charAt(i)) < RomanDigit.getInt(uRoman.charAt(i + 1))) {
				result -= RomanDigit.getInt(uRoman.charAt(i));
			} else {
				result += RomanDigit.getInt(uRoman.charAt(i));
			}
		}
		result += RomanDigit.getInt(uRoman.charAt(uRoman.length() - 1));
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + century;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Century other = (Century) obj;
		if (century != other.century) {
			return false;
		}
		return true;
	}

}

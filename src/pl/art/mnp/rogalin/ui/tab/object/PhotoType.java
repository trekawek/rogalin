package pl.art.mnp.rogalin.ui.tab.object;

public enum PhotoType {
	CURRENT("Fotografia bieżąca"), HISTORIC("Fotografia archiwalna");

	private final String label;

	private PhotoType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}
}

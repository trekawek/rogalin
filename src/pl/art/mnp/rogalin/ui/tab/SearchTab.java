package pl.art.mnp.rogalin.ui.tab;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchTab extends VerticalLayout {
	public SearchTab() {
		super();
		setMargin(true);
		addComponent(new Label("Tu znajdzie się formularz wyszukiwania"));
	}
}

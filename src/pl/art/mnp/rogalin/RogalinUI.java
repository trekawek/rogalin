package pl.art.mnp.rogalin;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
@Theme("rogalin")
@PreserveOnRefresh
public class RogalinUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Label title = new Label("Rogalin");
		title.addStyleName(Runo.LABEL_H1);
		title.setSizeUndefined();
		layout.addComponent(title);

		Label slogan = new Label("Baza danych obiektów muzealnych");
		slogan.addStyleName(Runo.LABEL_SMALL);
		slogan.setSizeUndefined();
		layout.addComponent(slogan);

		TabsController tabsController = new TabsController();
		layout.addComponent(tabsController.getTabs());
	}
}
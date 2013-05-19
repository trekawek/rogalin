package pl.art.mnp.rogalin;

import java.net.UnknownHostException;

import pl.art.mnp.rogalin.db.MongoDbProvider;

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
@Theme("runo")
@PreserveOnRefresh
public class RogalinUI extends UI {

	private MongoDbProvider dbProvider = new MongoDbProvider();

	@Override
	protected void init(VaadinRequest request) {
		try {
			dbProvider.connect();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Can't connect to mongo", e);
		}

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

		TabsController tabsController = new TabsController(dbProvider);
		layout.addComponent(tabsController.getTabs());
	}

	@Override
	public void close() {
		super.close();
		dbProvider.close();
	}
}
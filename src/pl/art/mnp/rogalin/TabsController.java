package pl.art.mnp.rogalin;

import java.io.Serializable;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.ui.tab.SearchTab;
import pl.art.mnp.rogalin.ui.tab.list.ObjectListTab;
import pl.art.mnp.rogalin.ui.tab.object.ObjectTab;
import pl.art.mnp.rogalin.ui.tab.options.OptionsTab;

import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class TabsController implements Serializable {

	private final ObjectListTab listTab;

	private final ObjectTab objectTab;

	private final SearchTab searchTab;

	private final OptionsTab optionsTab;

	private final TabSheet tabs;

	public TabsController(MongoDbProvider dbProvider) {
		tabs = new TabSheet();
		tabs.addTab(listTab = new ObjectListTab(dbProvider), "Obiekty");
		tabs.addTab(objectTab = new ObjectTab(dbProvider, this), "Dodaj nowy");
		tabs.addTab(searchTab = new SearchTab(), "Wyszukaj");
		tabs.addTab(optionsTab = new OptionsTab(dbProvider), "Kategorie");
	}

	public void switchToListTab(boolean reload) {
		if (reload) {
			listTab.refreshTable();
		}
		tabs.setSelectedTab(listTab);
	}

	TabSheet getTabs() {
		return tabs;
	}
}

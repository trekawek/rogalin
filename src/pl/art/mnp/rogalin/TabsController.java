package pl.art.mnp.rogalin;

import java.io.Serializable;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.ui.tab.ObjectForm;
import pl.art.mnp.rogalin.ui.tab.ObjectList;
import pl.art.mnp.rogalin.ui.tab.OptionsTab;
import pl.art.mnp.rogalin.ui.tab.SearchTab;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabsController implements Serializable {

	private final ObjectList listTab;

	private final TabSheet tabs;

	public TabsController(MongoDbProvider dbProvider) {
		tabs = new TabSheet();
		tabs.addTab(listTab = new ObjectList(dbProvider), "Obiekty");
		VerticalLayout newObjectLayout = new VerticalLayout(new ObjectForm(dbProvider, new Runnable() {
			@Override
			public void run() {
				switchToListTab(true);
			}
		}));
		newObjectLayout.setMargin(true);
		tabs.addTab(newObjectLayout, "Dodaj nowy");
		tabs.addTab(new SearchTab(), "Wyszukaj");
		tabs.addTab(new OptionsTab(dbProvider), "Kategorie");
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
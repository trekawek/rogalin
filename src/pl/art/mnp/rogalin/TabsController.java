package pl.art.mnp.rogalin;

import java.io.Serializable;
import java.util.List;

import pl.art.mnp.rogalin.db.predicate.Predicate;
import pl.art.mnp.rogalin.ui.tab.ObjectForm;
import pl.art.mnp.rogalin.ui.tab.ObjectList;
import pl.art.mnp.rogalin.ui.tab.OptionsTab;
import pl.art.mnp.rogalin.ui.tab.SaveActionListener;
import pl.art.mnp.rogalin.ui.tab.SearchTab;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

public class TabsController implements Serializable, SelectedTabChangeListener {

	private static final long serialVersionUID = -545289520470774425L;

	private final TabSheet tabs;

	private Component previousSelectedTab;

	private final ObjectList listTab;

	private Component newObjectTab;

	private SearchTab searchTab;

	private OptionsTab optionsTab;

	public TabsController() {
		tabs = new TabSheet();
		tabs.addTab(listTab = createListTab(), "Obiekty");
		tabs.addTab(newObjectTab = createNewObjectTab(), "Dodaj nowy");
		tabs.addTab(searchTab = createSearchTab(), "Wyszukaj");
		tabs.addTab(optionsTab = createOptionsTab(), "Kategorie");
		listTab.setShowAllListener(searchTab);
		tabs.addSelectedTabChangeListener(this);
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

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		if (previousSelectedTab == optionsTab) {
			tabs.replaceComponent(newObjectTab, newObjectTab = createNewObjectTab());
			tabs.replaceComponent(searchTab, searchTab = createSearchTab());
			listTab.setShowAllListener(searchTab);
		}
		previousSelectedTab = tabs.getSelectedTab();
	}

	private ObjectList createListTab() {
		return new ObjectList();
	}

	private Component createNewObjectTab() {
		VerticalLayout newObjectLayout = new VerticalLayout(new ObjectForm(new SaveActionListener() {
			private static final long serialVersionUID = -848485094498394290L;

			@Override
			public void onSaveAction() {
				switchToListTab(true);
			}
		}));
		newObjectLayout.setMargin(true);
		return newObjectLayout;
	}

	private SearchTab createSearchTab() {
		return new SearchTab(new PredicateListener() {
			private static final long serialVersionUID = 777576939494883965L;

			@Override
			public void gotPredicates(List<Predicate> predicates) {
				listTab.gotPredicates(predicates);
				switchToListTab(false);
			}
		});
	}

	private OptionsTab createOptionsTab() {
		return new OptionsTab();
	}

	public static interface PredicateListener extends Serializable {
		void gotPredicates(List<Predicate> predicates);
	}
}
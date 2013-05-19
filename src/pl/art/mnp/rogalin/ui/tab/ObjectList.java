package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.vaadin.dialogs.ConfirmDialog;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.DBObject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ObjectList extends VerticalLayout implements Handler, SaveActionListener {

	private static final Action PREVIEW = new Action("Zobacz");

	private static final Action EDIT = new Action("Edytuj");

	private static final Action REMOVE = new Action("Usuń");

	private static final Action[] ACTIONS = new Action[] { PREVIEW, EDIT, REMOVE };

	private static final Set<FieldInfo> VISIBLE_COLUMNS = EnumSet.of(FieldInfo.IDENTIFIER, FieldInfo.NAME,
			FieldInfo.TYPE, FieldInfo.EVALUATION_DATE);

	private final VerticalLayout layout;

	private final ObjectsDao objectDao;

	private final MongoDbProvider dbProvider;

	private String query;

	private Table table;

	public ObjectList(MongoDbProvider dbProvider) {
		super();
		this.dbProvider = dbProvider;
		this.objectDao = dbProvider.getObjectsProvider();

		setMargin(true);
		setSpacing(true);
		setWidth("100%");

		layout = new VerticalLayout();
		layout.addComponent(searchField());
		layout.addComponent(renderTable());
		addComponent(layout);
	}

	private Component searchField() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		final TextField searchText = new TextField();
		searchText.setImmediate(true);
		searchText.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				filterResults(searchText.getValue());
			}
		});
		Button searchButton = new Button("Wyszukaj");
		searchButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				filterResults(searchText.getValue());
			}
		});
		Button resetSearch = new Button("Pokaż wszystkie");
		resetSearch.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				searchText.setValue("");
			}
		});
		layout.addComponents(searchText, searchButton, resetSearch);
		return layout;
	}

	private void filterResults(String value) {
		if (StringUtils.isEmpty(value)) {
			this.query = null;
		} else {
			this.query = value;
		}
		refreshTable();
	}

	private Table renderTable() {
		table = new Table();
		table.setWidth("100%");
		table.setStyleName(Runo.TABLE_SMALL);
		table.setSelectable(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setImmediate(true);
		table.setMultiSelect(false);
		table.addActionHandler(this);
		table.setSelectable(false);
		table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					DBObject object = objectDao.getObject(event.getItemId());
					showPreview(object);
				}
			}
		});
		for (FieldInfo f : FieldInfo.values()) {
			table.addContainerProperty(f, String.class, "-");
			table.setColumnCollapsed(f, !VISIBLE_COLUMNS.contains(f));
		}

		refreshTable();
		return table;
	}

	public void refreshTable() {
		table.removeAllItems();
		List<DBObject> objects;
		if (StringUtils.isEmpty(query)) {
			objects = objectDao.getObjectList();
		} else {
			objects = objectDao.getFilteredObjectList(query);
		}
		for (DBObject o : objects) {
			table.addItem(createTableRow(o), o.get("_id"));
		}
	}

	private Object[] createTableRow(DBObject dbObject) {
		List<Object> properties = new ArrayList<Object>(FieldInfo.values().length);
		for (FieldInfo f : FieldInfo.values()) {
			properties.add(f.getStringValue(dbObject));
		}
		return properties.toArray();
	}

	private final ClickListener showTable = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			removeAllComponents();
			addComponent(layout);
		}
	};

	@Override
	public Action[] getActions(Object target, Object sender) {
		return ACTIONS;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (sender == table) {
			DBObject object = objectDao.getObject(target);
			if (action == PREVIEW) {
				showPreview(object);
			} else if (action == EDIT) {
				showEditView(object);
			} else if (action == REMOVE) {
				remove(object);
			}
		}
	}

	private void showPreview(DBObject object) {
		if (object == null) {
			return;
		}
		removeAllComponents();

		Button back = new Button("Powrót do listy");
		back.addClickListener(showTable);
		addComponent(back);

		ObjectPreview preview = new ObjectPreview(object, objectDao);
		addComponent(preview);
	}

	private void showEditView(DBObject object) {
		if (object == null) {
			return;
		}
		removeAllComponents();

		Button back = new Button("Powrót do listy");
		back.addClickListener(showTable);
		addComponent(back);

		ObjectForm objectForm = new ObjectForm(dbProvider, this, object);
		addComponent(objectForm);
	}

	private void remove(final DBObject object) {
		ConfirmDialog.show(this.getUI(), "Potwierdzenie", "Czy na pewno chcesz usunąć ten obiekt?", "OK",
				"Anuluj", new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog dialog) {
						if (!dialog.isConfirmed()) {
							return;
						}
						if (object != null) {
							objectDao.removeObject(object);
							refreshTable();
						}
					}
				});
	}

	@Override
	public void onSaveAction() {
		refreshTable();
		removeAllComponents();
		addComponent(layout);
	}
}
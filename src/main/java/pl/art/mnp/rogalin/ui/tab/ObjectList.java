package pl.art.mnp.rogalin.ui.tab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.vaadin.dialogs.ConfirmDialog;

import pl.art.mnp.rogalin.PathUtils;
import pl.art.mnp.rogalin.RogalinUI;
import pl.art.mnp.rogalin.TabsController.PredicateListener;
import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ObjectList extends VerticalLayout implements Handler, PredicateListener {

	private static final Action PREVIEW = new Action("Zobacz");

	private static final Action EDIT = new Action("Edytuj");

	private static final Action REMOVE = new Action("Usuń");

	private static final Action[] ACTIONS_READONLY = new Action[] { PREVIEW };

	private static final Action[] ACTIONS = new Action[] { PREVIEW, EDIT, REMOVE };

	private static final Action[] ACTIONS_WITHOUT_REMOVE = new Action[] { PREVIEW, EDIT };

	private static final Set<FieldInfo> VISIBLE_COLUMNS = EnumSet.of(FieldInfo.IDENTIFIER, FieldInfo.NAME,
			FieldInfo.TYPE, FieldInfo.EVALUATION_DATE);

	private final VerticalLayout layout;

	private final String defaultHome;

	private TextField searchText;

	private ShowAllListener showAllListener;

	private Label filterInfo;

	private List<Predicate> predicates;

	private String query;

	private Table table;

	private List<ObjectId> objectIds;

	private Set<Integer> fragmentIndices;

	private boolean showFragments = true;

	public ObjectList() {
		super();
		List<String> homeOptions = DbConnection.getInstance().getOptionsDao().getOptions(FieldInfo.HOME);
		if (homeOptions.isEmpty()) {
			defaultHome = null;
		} else {
			defaultHome = homeOptions.get(0);
		}
		this.showAllListener = new ShowAllListener() {
			private static final long serialVersionUID = -5072458379584711625L;

			@Override
			public void showAll() {
			}
		};

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
		searchText = new TextField();
		searchText.setImmediate(true);
		searchText.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 6289526628390104589L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				filterResults(searchText.getValue());
			}
		});
		Button searchButton = new Button("Wyszukaj");
		searchButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -1583248192476499700L;

			@Override
			public void buttonClick(ClickEvent event) {
				filterResults(searchText.getValue());
			}
		});
		final CheckBox showFragmentsCheckbox = new CheckBox("Pokaż fragmenty");
		showFragmentsCheckbox.setValue(true);
		showFragmentsCheckbox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				showFragments = (Boolean) event.getProperty().getValue();
				refreshTable();
			}
		});
		Button resetSearch = new Button("Pokaż wszystkie");
		resetSearch.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 934242241334057589L;

			@Override
			public void buttonClick(ClickEvent event) {
				predicates = null;
				if (StringUtils.isEmpty(searchText.getValue())) {
					refreshTable();
				} else {
					searchText.setValue("");
				}
				showAllListener.showAll();
			}
		});
		Button excel = new Button("Eksportuj");
		excel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 934242241334057589L;

			@Override
			public void buttonClick(ClickEvent event) {
				exportToExcel();
			}
		});

		filterInfo = new Label("");
		layout.addComponents(searchText, searchButton, resetSearch, showFragmentsCheckbox, excel, filterInfo);
		return layout;
	}

	private void exportToExcel() {
		ExcelExport export = new ExcelExport(table);
		export.setExportFileName("rogalin-obiekty.xls");
		export.excludeCollapsedColumns();
		export.export();
		export.sendConverted();
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
			private static final long serialVersionUID = 236557764476429213L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					showPreview((Integer) event.getItemId());
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
		int firstItemIndex = table.getCurrentPageFirstItemIndex();
		table.removeAllItems();
		Collection<DBObject> objectList;
		ObjectsDao objectDao = DbConnection.getInstance().getObjectsDao();
		if (StringUtils.isEmpty(query)) {
			objectList = objectDao.getObjectList();
		} else {
			objectList = objectDao.getFilteredObjectList(query);
		}
		if (showFragments) {
			objectList = enhanceWithFragments(objectList);
		}

		int i = 0;
		objectIds = new ArrayList<ObjectId>();
		fragmentIndices = new HashSet<Integer>();
		for (DBObject o : objectList) {
			if ((predicates == null && matchesDefaultPredicates(o))
					|| (predicates != null && matchesPredicates(o))) {
				objectIds.add((ObjectId) o.get("_id"));
				Object[] row = createTableRow(o);
				if (o.containsField("isFragment")) {
					fragmentIndices.add(i);
				}
				table.addItem(row, i++);
			}
		}
		table.setCurrentPageFirstItemIndex(firstItemIndex);
		filterInfo.setValue(String.format("Ilość rezultatów: %d. %s", i, predicates == null ? ""
				: "Wyniki podlegają filtrowaniu."));
	}

	private List<DBObject> enhanceWithFragments(Collection<DBObject> objects) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (DBObject o : objects) {
			list.add(o);
			list.addAll(getFragments(o));
		}
		return list;
	}

	private List<DBObject> getFragments(DBObject parent) {
		List<DBObject> fragmentList = new ArrayList<DBObject>();

		DBObject fragments = (DBObject) parent.get("fragments");
		if (fragments == null) {
			return fragmentList;
		}

		BasicDBList list = (BasicDBList) fragments.get("items");
		for (Object rawObject : list) {
			DBObject fragment = (DBObject) rawObject;
			DBObject newItem = new BasicDBObject(parent.toMap());
			String newName = String
					.format("%s (%s)", parent.get(FieldInfo.NAME.name()), fragment.get("name"));
			newItem.put(FieldInfo.NAME.name(), newName);
			newItem.put(FieldInfo.CONTAINER_NO.name(), fragment.get(FieldInfo.CONTAINER_NO.name()));
			newItem.put(FieldInfo.CONTAINER_SEGMENT.name(), fragment.get(FieldInfo.CONTAINER_SEGMENT.name()));
			newItem.put("isFragment", true);
			fragmentList.add(newItem);
		}
		return fragmentList;
	}

	private boolean matchesDefaultPredicates(DBObject o) {
		if (StringUtils.isBlank(defaultHome)) {
			return true;
		}
		String location = (String) o.get(FieldInfo.HOME.name());
		return defaultHome.equals(location);
	}

	private boolean matchesPredicates(DBObject o) {
		for (Predicate p : predicates) {
			if (!p.matches(o)) {
				return false;
			}
		}
		return true;
	}

	private Object[] createTableRow(DBObject dbObject) {
		List<Object> properties = new ArrayList<Object>(FieldInfo.values().length);
		for (FieldInfo f : FieldInfo.values()) {
			properties.add(f.getFieldType().getValue(dbObject));
		}
		return properties.toArray();
	}

	private final ClickListener showTable = new ClickListener() {
		private static final long serialVersionUID = -1863572674350666127L;

		@Override
		public void buttonClick(ClickEvent event) {
			removeAllComponents();
			addComponent(layout);
		}
	};

	@Override
	public Action[] getActions(Object target, Object sender) {
		if (RogalinUI.READONLY) {
			return ACTIONS_READONLY;
		} else if (fragmentIndices.contains(target)) {
			return ACTIONS_WITHOUT_REMOVE;
		} else {
			return ACTIONS;
		}
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (sender == table) {
			final int index = (Integer) target;
			if (action == PREVIEW) {
				showPreview(index);
			} else if (action == EDIT) {
				showEditView(index);
			} else if (action == REMOVE) {
				remove(index);
			}
		}
	}

	private DBObject getObjectByIndex(int index) {
		ObjectId id = objectIds.get(index);
		return DbConnection.getInstance().getObjectsDao().getObject(id);
	}

	private void showPreview(int index) {
		final DBObject object = getObjectByIndex(index);
		if (object == null) {
			return;
		}
		removeAllComponents();
		addComponent(getPreviewButtons(object, index));

		ObjectPreview preview = new ObjectPreview(object);
		addComponent(preview);
	}

	private void showEditView(int index) {
		final DBObject object = getObjectByIndex(index);
		if (object == null) {
			return;
		}
		removeAllComponents();
		addComponent(getEditButtons(object));

		ObjectForm objectForm = new ObjectForm(new SaveActionListener() {
			private static final long serialVersionUID = 8084094197862972496L;

			@Override
			public void onSaveAction() {
				refreshTable();
				removeAllComponents();
				addComponent(layout);
			}
		}, object);
		addComponent(objectForm);
	}

	private void remove(int index) {
		final DBObject object = getObjectByIndex(index);
		ConfirmDialog.show(this.getUI(), "Potwierdzenie", "Czy na pewno chcesz usunąć ten obiekt?", "OK",
				"Anuluj", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = -3518627734961298673L;

					@Override
					public void onClose(ConfirmDialog dialog) {
						if (!dialog.isConfirmed()) {
							return;
						}
						if (object != null) {
							DbConnection.getInstance().getObjectsDao().removeObject(object);
							refreshTable();
						}
					}
				});
	}

	@Override
	public void gotPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
		if (!StringUtils.isEmpty(this.searchText.getValue())) {
			this.searchText.setValue("");
		} else {
			this.refreshTable();
		}
	}

	public void setShowAllListener(ShowAllListener listener) {
		this.showAllListener = listener;
	}

	public static interface ShowAllListener extends Serializable {
		void showAll();
	}

	private Component getEditButtons(DBObject object) {
		HorizontalLayout layout = new HorizontalLayout();

		Button back = new Button("Powrót do listy");
		back.addClickListener(showTable);
		layout.addComponent(back);
		layout.addComponent(getPrintButton(object));

		return layout;
	}

	private Component getPreviewButtons(final DBObject object, final int index) {
		HorizontalLayout layout = new HorizontalLayout();

		Button back = new Button("Powrót do listy");
		back.addClickListener(showTable);
		layout.addComponent(back);
		layout.addComponent(getPrintButton(object));

		if (!RogalinUI.READONLY) {
			Button edit = new Button("Edytuj");
			edit.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					showEditView(index);
				}
			});
			layout.addComponent(edit);
		}

		Button prev = new Button("<<");
		prev.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showPreview(index - 1);
			}
		});
		if (index <= 0) {
			prev.setEnabled(false);
		}
		Button next = new Button(">>");
		next.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showPreview(index + 1);
			}
		});
		if (index >= (objectIds.size() - 1)) {
			next.setEnabled(false);
		}
		layout.addComponent(prev);
		layout.addComponent(next);

		return layout;
	}

	private Button getPrintButton(DBObject dbObject) {
		BrowserWindowOpener opener = new BrowserWindowOpener(PathUtils.getServletPath() + "/print");
		opener.setParameter("objectId", dbObject.get("_id").toString());
		opener.setFeatures("height=200,width=400,resizable");
		Button print = new Button("Drukuj");
		opener.extend(print);
		return print;
	}
}
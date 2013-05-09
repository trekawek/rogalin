package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.DBObject;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ObjectList extends VerticalLayout {

	private static final Set<FieldInfo> VISIBLE_COLUMNS = EnumSet.of(FieldInfo.IDENTIFIER, FieldInfo.NAME, FieldInfo.TYPE,
			FieldInfo.EVALUATION_DATE);

	private final VerticalLayout layout;

	private final ObjectsDao objectDao;

	private final MongoDbProvider dbProvider;

	private Table table;

	public ObjectList(MongoDbProvider dbProvider) {
		super();
		this.dbProvider = dbProvider;
		this.objectDao = dbProvider.getObjectsProvider();

		setMargin(true);
		setSpacing(true);
		setWidth("100%");

		layout = new VerticalLayout();
		layout.addComponent(renderButtons());
		layout.addComponent(renderTable());
		addComponent(layout);
	}

	private HorizontalLayout renderButtons() {
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		Button previewButton = new Button("Zobacz");
		previewButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showPreview();
			}
		});
		Button editButton = new Button("Edytuj");
		editButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showEditView();
			}
		});
		Button removeButton = new Button("Usuń");
		removeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				remove();
			}
		});
		buttons.addComponents(previewButton, editButton, removeButton);
		buttons.setMargin(new MarginInfo(false, false, true, false));
		return buttons;
	}

	protected void remove() {
		DBObject selected = getSelectedObject();
		if (selected != null) {
			objectDao.removeObject(selected);
			refreshTable();
		}
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
		for (FieldInfo f : FieldInfo.values()) {
			table.addContainerProperty(f, String.class, "-");
			table.setColumnCollapsed(f, !VISIBLE_COLUMNS.contains(f));
		}

		refreshTable();
		return table;
	}

	public void refreshTable() {
		table.removeAllItems();
		List<DBObject> objects = objectDao.getObjectList();
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

	private void showPreview() {
		DBObject object = getSelectedObject();
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

	private void showEditView() {
		DBObject object = getSelectedObject();
		if (object == null) {
			return;
		}
		removeAllComponents();

		Button back = new Button("Powrót do listy");
		back.addClickListener(showTable);
		addComponent(back);

		ObjectForm objectForm = new ObjectForm(dbProvider, new Runnable() {
			@Override
			public void run() {
				removeAllComponents();
				addComponent(layout);
			}
		}, object);
		addComponent(objectForm);
	}

	private DBObject getSelectedObject() {
		Object itemId = table.getValue();
		if (itemId == null) {
			Notification.show("Nie wybrano obiektu", Type.ERROR_MESSAGE);
			return null;
		}
		return objectDao.getObject(itemId);
	}

	private final ClickListener showTable = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			removeAllComponents();
			addComponent(layout);
		}
	};
}
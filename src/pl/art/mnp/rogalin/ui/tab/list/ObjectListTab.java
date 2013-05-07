package pl.art.mnp.rogalin.ui.tab.list;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.model.Field;

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
public class ObjectListTab extends VerticalLayout {

	private static final Set<Field> VISIBLE_COLUMNS = EnumSet.of(Field.IDENTIFIER, Field.NAME, Field.TYPE,
			Field.EVALUATION_DATE);

	private final VerticalLayout layout;

	private final ObjectsDao objectDao;

	private Table table;

	public ObjectListTab(MongoDbProvider dbProvider) {
		super();
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
		for (Field f : Field.values()) {
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
		List<Object> properties = new ArrayList<Object>(Field.values().length);
		for (Field f : Field.values()) {
			properties.add(f.getStringValue(dbObject));
		}
		return properties.toArray();
	}

	private void showPreview() {
		DBObject object = getSelectedObject();
		if (object == null) {
			return;
		}
		ObjectPreview preview = new ObjectPreview(object, objectDao, showTable);
		removeAllComponents();
		addComponent(preview);
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
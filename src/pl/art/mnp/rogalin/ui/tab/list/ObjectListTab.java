package pl.art.mnp.rogalin.ui.tab.list;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.Field;

import com.mongodb.DBObject;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class ObjectListTab extends HorizontalLayout implements ItemClickListener {

	private static final Set<Field> VISIBLE_COLUMNS = EnumSet.of(Field.IDENTIFIER, Field.NAME, Field.TYPE,
			Field.EVALUATION_DATE);

	private final MongoDbProvider dbProvider;

	public ObjectListTab(MongoDbProvider dbProvider) {
		super();
		this.dbProvider = dbProvider;

		setMargin(true);
		setSpacing(true);
		setWidth("100%");

		Table table = createTable();
		addComponent(table);
	}

	private Table createTable() {
		Table t = new Table();
		t.setWidth("100%");
		t.setStyleName(Runo.TABLE_SMALL);
		t.setSelectable(true);
		t.setColumnCollapsingAllowed(true);
		t.setColumnReorderingAllowed(true);
		t.setImmediate(true);
		for (Field f : Field.values()) {
			t.addContainerProperty(f, String.class, "-");
			t.setColumnCollapsed(f, !VISIBLE_COLUMNS.contains(f));
		}

		List<DBObject> objects = dbProvider.getObjectsProvider().getObjectList();
		for (DBObject o : objects) {
			t.addItem(createTableRow(o), o.get("_id"));
		}

		t.addItemClickListener(this);
		return t;
	}

	private Object[] createTableRow(DBObject dbObject) {
		List<Object> properties = new ArrayList<Object>(Field.values().length);
		for (Field f : Field.values()) {
			properties.add(f.getStringValue(dbObject));
		}
		return properties.toArray();
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		Object itemId = event.getItemId();
		DBObject dbObject = dbProvider.getObjectsProvider().getObject(itemId);

		Window w = new Window("Podgląd obiektu", new ObjectPreview(dbObject));
		w.setPositionX(50);
		w.setPositionY(50);

		UI.getCurrent().addWindow(w);
	}
}
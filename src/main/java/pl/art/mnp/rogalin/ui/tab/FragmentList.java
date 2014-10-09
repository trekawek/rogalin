package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import pl.art.mnp.rogalin.db.FieldInfo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

public class FragmentList extends VerticalLayout implements Handler {

	private static final long serialVersionUID = 4707165482047426981L;

	private final List<Object[]> items = new ArrayList<Object[]>();

	private final DBObject parentObject;

	private Table table;

	public FragmentList(DBObject object, boolean readOnly) {
		super();
		this.parentObject = object;

		loadFragments(object);
		if (items.isEmpty() && readOnly) {
			return;
		}

		Label label = new Label("Fragmenty");
		label.addStyleName(Runo.LABEL_H2);
		label.setSizeUndefined();
		addComponent(label);

		table = new Table();
		table.setWidth("40%");
		table.setStyleName(Runo.TABLE_SMALL);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setImmediate(true);
		table.setMultiSelect(false);
		table.setHeight("100px");

		table.addContainerProperty("Nazwa", String.class, "-");
		table.addContainerProperty(FieldInfo.CONTAINER_NO, String.class, "-");
		table.addContainerProperty(FieldInfo.CONTAINER_SEGMENT, String.class, "-");
		refreshTable();
		addComponent(table);

		if (readOnly) {
			return;
		}
		table.addActionHandler(this);

		FormLayout form = new FormLayout();
		final TextField nameField = new TextField("Nazwa");
		nameField.setRequired(true);

		form.addComponent(nameField);
		final ComboBox containerNoField = (ComboBox) FieldInfo.CONTAINER_NO.getFieldType().getFormField()
				.getComponent();
		final ComboBox containerSegmentField = (ComboBox) FieldInfo.CONTAINER_SEGMENT.getFieldType()
				.getFormField().getComponent();
		form.addComponent(containerNoField);
		form.addComponent(containerSegmentField);

		Button addButton = new Button("Dodaj fragment");
		addButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -4937856160482125393L;

			@Override
			public void buttonClick(ClickEvent event) {
				Object[] newRow = new Object[3];
				newRow[0] = nameField.getValue();
				newRow[1] = containerNoField.getValue();
				newRow[2] = containerSegmentField.getValue();

				nameField.setValue("");
				containerNoField.setValue("");
				containerSegmentField.setValue("");

				items.add(newRow);
				refreshTable();
			}
		});
		form.addComponent(addButton);
		addComponent(form);
	}

	public DBObject getFragments() {
		BasicDBObject fragments = new BasicDBObject();
		fragments.put("parentId", parentObject.get("_id"));

		BasicDBList list = new BasicDBList();
		fragments.put("items", list);
		for (Object[] i : items) {
			BasicDBObject o = new BasicDBObject();
			o.put("name", i[0]);
			o.put(FieldInfo.CONTAINER_NO.name(), i[1]);
			o.put(FieldInfo.CONTAINER_SEGMENT.name(), i[2]);
			list.add(o);
		}
		return fragments;
	}

	public void loadFragments(DBObject dbObject) {
		items.clear();
		if (dbObject == null) {
			return;
		}
		DBObject fragments = (BasicDBObject) dbObject.get("fragments");
		if (fragments == null) {
			return;
		}
		BasicDBList list = (BasicDBList) fragments.get("items");
		for (Object rawObject : list) {
			DBObject o = (DBObject) rawObject;
			Object[] item = new Object[3];
			item[0] = o.get("name");
			item[1] = o.get(FieldInfo.CONTAINER_NO.name());
			item[2] = o.get(FieldInfo.CONTAINER_SEGMENT.name());
			items.add(item);
		}
	}

	private void refreshTable() {
		table.removeAllItems();
		for (int i = 0; i < items.size(); i++) {
			table.addItem(items.get(i), i);
		}
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		return new Action[] { new Action("Usuń") };
	}

	@Override
	public void handleAction(Action action, Object sender, final Object target) {
		ConfirmDialog.show(this.getUI(), "Potwierdzenie", "Czy na pewno chcesz usunąć ten fragment?", "OK",
				"Anuluj", new ConfirmDialog.Listener() {
					private static final long serialVersionUID = -3518627734961298673L;

					@Override
					public void onClose(ConfirmDialog dialog) {
						if (!dialog.isConfirmed()) {
							return;
						}
						items.remove(((Integer) target).intValue());
						refreshTable();
					}
				});
	}
}

package pl.art.mnp.rogalin.ui.tab.list;

import pl.art.mnp.rogalin.model.Field;

import com.mongodb.DBObject;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

@SuppressWarnings("serial")
public class ObjectPreview extends FormLayout {

	public ObjectPreview(DBObject dbObject) {
		setMargin(true);
		setSpacing(true);

		GridLayout columns = new GridLayout(2, 1);
		columns.setSpacing(true);
		FormLayout leftColumn = new FormLayout();
		FormLayout rightColumn = new FormLayout();
		FormLayout belowColumns = new FormLayout();
		columns.addComponent(leftColumn, 0, 0);
		columns.addComponent(rightColumn, 1, 0);
		addComponent(columns);
		addComponent(belowColumns);

		int i = 0;
		for (Field f : Field.values()) {
			Layout container;
			if (f.isBelowColumns()) {
				container = belowColumns;
			} else if (i <= 12) {
				container = leftColumn;
				i++;
			} else {
				container = rightColumn;
			}

			Label field = new Label();
			field.setCaption(f.toString());
			field.setValue(f.getStringValue(dbObject));
			container.addComponent(field);
		}
	}
}
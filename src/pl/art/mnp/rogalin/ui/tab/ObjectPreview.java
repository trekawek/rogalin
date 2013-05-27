package pl.art.mnp.rogalin.ui.tab;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;

import com.mongodb.DBObject;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ObjectPreview extends VerticalLayout {

	public ObjectPreview(DBObject dbObject) {
		super();
		this.setStyleName("object-preview");
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
		for (FieldInfo f : FieldInfo.values()) {
			Layout container;
			if (f.isBelowColumns()) {
				container = belowColumns;
			} else if (i <= 14) {
				container = leftColumn;
				i++;
			} else {
				container = rightColumn;
			}

			boolean visible = true;
			if (f.getDependsOn() != null) {
				Object value = dbObject.get(f.getDependsOn().name());
				if (!f.isVisible(value)) {
					visible = false;
				}
			}
			if (visible) {
				container.addComponent(f.getFieldType().getPreviewField(dbObject));
			}
		}

		GridLayout photos = new GridLayout(2, 1);
		photos.setSpacing(true);
		for (DbPhoto p : DbConnection.getInstance().getObjectsDao().getPhotos(dbObject)) {
			photos.addComponent(renderPhoto(p));
		}
		addComponent(photos);
	}

	private Layout renderPhoto(DbPhoto p) {
		Layout layout = new VerticalLayout();

		Link link = new Link("", p.getResource());
		link.setIcon(p.getThumbnailResource());
		link.setTargetName("_blank");
		layout.addComponent(link);

		String type = p.getType().toString();
		layout.addComponent(new Label(type));
		return layout;
	}
}
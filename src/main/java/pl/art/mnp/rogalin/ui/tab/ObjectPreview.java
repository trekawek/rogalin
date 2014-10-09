package pl.art.mnp.rogalin.ui.tab;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;

import com.mongodb.DBObject;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class ObjectPreview extends VerticalLayout {

	private static final long serialVersionUID = 8078600605367926111L;

	public ObjectPreview(DBObject dbObject) {
		super();
		this.setStyleName("object-preview");
		GridLayout columns = new GridLayout(2, 1);
		columns.setSpacing(true);
		FormLayout leftColumn = new FormLayout();
		leftColumn.setWidth("400px");
		FormLayout rightColumn = new FormLayout();
		rightColumn.setWidth("400px");
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
			} else if (i <= 16) {
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

		addComponent(new FragmentList(dbObject, true));

		Label label = new Label("Fotografie");
		label.addStyleName(Runo.LABEL_H2);
		label.setSizeUndefined();
		addComponent(label);

		VerticalLayout photos = new VerticalLayout();
		photos.setSpacing(true);
		HorizontalLayout horizLayout = new HorizontalLayout();
		horizLayout.setSpacing(true);
		for (DbPhoto p : DbConnection.getInstance().getObjectsDao().getPhotos(dbObject)) {
			horizLayout.addComponent(renderPhoto(p));
			if (horizLayout.getComponentCount() == 2) {
				photos.addComponent(horizLayout);
				horizLayout = new HorizontalLayout();
				horizLayout.setSpacing(true);
			}
		}
		if (horizLayout.getComponentCount() > 0) {
			photos.addComponent(horizLayout);
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
package pl.art.mnp.rogalin.ui.tab.list;

import pl.art.mnp.rogalin.db.ObjectsDao;
import pl.art.mnp.rogalin.model.Field;
import pl.art.mnp.rogalin.model.Photo;

import com.mongodb.DBObject;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ObjectPreview extends VerticalLayout {

	public ObjectPreview(DBObject dbObject, ObjectsDao objectProvider, ClickListener clickListener) {
		super();
		Button back = new Button("Powrót do listy");
		back.addClickListener(clickListener);
		addComponent(back);

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

		GridLayout photos = new GridLayout(4, 1);
		photos.setSpacing(true);
		for (Photo p : objectProvider.getPhotos(dbObject)) {
			photos.addComponent(renderPhoto(p));
		}
		addComponent(photos);
	}

	private Layout renderPhoto(Photo p) {
		Layout layout = new VerticalLayout();
		StreamResource source = new StreamResource(p, p.getName());
		Image image = new Image(p.getName(), source);
		image.setWidth("150px");
		layout.addComponent(image);
		Link link = new Link("Pełny rozmiar", source);
		link.setTargetName("_blank");
		layout.addComponent(link);
		return layout;
	}
}
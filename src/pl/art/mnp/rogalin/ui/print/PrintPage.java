package pl.art.mnp.rogalin.ui.print;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;

import com.mongodb.DBObject;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("rogalin")
public class PrintPage extends UI {

	private static final long serialVersionUID = -7292399835410943468L;

	private final VerticalLayout content;

	public PrintPage() {
		super();
		this.content = new VerticalLayout();
	}

	@Override
	protected void init(VaadinRequest request) {
		ObjectId objectId = new ObjectId(request.getParameter("objectId"));
		DBObject dbObject = DbConnection.getInstance().getObjectsDao().getObject(objectId);

		Label objectInfo = new Label(new ObjectRenderer(dbObject).toString(), ContentMode.HTML);
		content.addComponent(objectInfo);

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
		content.addComponent(photos);
		setContent(content);

		JavaScript.getCurrent().execute("setTimeout(function() {" + "  print(); self.close();}, 1000);");
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

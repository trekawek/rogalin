package pl.art.mnp.rogalin.db;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.ui.photo.DbPhoto;
import pl.art.mnp.rogalin.ui.tab.OptionsTab;

import com.mongodb.DBObject;
import com.vaadin.ui.Notification;

public final class ManagementUtils {

	protected static final Logger LOG = Logger.getLogger(OptionsTab.class.getName());

	private ManagementUtils() {
	}

	public static void generateThumbnails() {
		DbConnection connection = DbConnection.getInstance();
		ObjectsDao dao = connection.getObjectsDao();
		try {
			for (DBObject o : dao.getObjectList()) {
				LOG.info((String) o.get(FieldInfo.NAME.name()));
				List<DbPhoto> photos = dao.getPhotos(o);
				for (DbPhoto p : photos) {
					p.generateThumbnail();
				}
			}
			Notification.show("Wygenerowano miniatury", Notification.Type.HUMANIZED_MESSAGE);
		} catch (IOException e) {
			Notification.show("Wystąpił błąd", Notification.Type.ERROR_MESSAGE);
			LOG.log(Level.WARNING, "Can't generate thumbnails", e);
		}
	}

}

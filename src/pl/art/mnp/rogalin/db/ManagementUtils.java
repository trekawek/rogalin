package pl.art.mnp.rogalin.db;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.ui.photo.DbPhoto;
import pl.art.mnp.rogalin.ui.tab.OptionsTab;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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

	public static void changeFieldToMultiselect(FieldInfo field) {
		DBCollection collection = DbConnection.getInstance().getMongoDb().getCollection(ObjectsDao.OBJECTS);
		for (DBObject o : collection.find().toArray()) {
			Object obj = o.get(field.name());
			if (obj instanceof String) {
				DBObject newObj = new BasicDBObject();
				newObj.put("values", Arrays.asList(obj));
				o.put(field.name(), newObj);
				collection.update(new BasicDBObject("_id", o.get("_id")), o);
			}
		}
	}
}

package pl.art.mnp.rogalin.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

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

	public static void generateThumbnails(DbConnection connection) {
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

	public static void changeFieldToMultiselect(DbConnection connection, FieldInfo field) {
		DBCollection collection = connection.getMongoDb().getCollection(ObjectsDao.OBJECTS);
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

	public static void importLocations(DbConnection connection) {
		InputStream is = ManagementUtils.class.getClassLoader().getResourceAsStream("locations.txt");
		if (is == null) {
			System.err.println("can't find locations");
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		DBCollection collection = connection.getMongoDb().getCollection("objects");
		Set<String> used = new HashSet<String>();
		try {
			while ((line = reader.readLine()) != null) {
				String location = line;
				String objects = reader.readLine();
				updateLocation(collection, used, location, objects);
				reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateLocation(DBCollection collection, Set<String> used, String location,
			String objects) throws Exception {
		String[] list = objects.split(", ");
		System.out.println(" *** " + location + " *** ");
		for (String objectIdentifier : list) {
			DBObject ref = new BasicDBObject(FieldInfo.IDENTIFIER.name(), "P " + objectIdentifier);
			DBObject object = collection.findOne(ref);
			if (object == null) {
				System.err.println("Can't find " + objectIdentifier + " (location: " + location + ")");
			} else if (!StringUtils.isEmpty((String) object.get(FieldInfo.LOCATION.name()))) {
				System.err.println("Location already exists for " + objectIdentifier + " (location: "
						+ location + ")");
			} else {
				object.put(FieldInfo.LOCATION.name(), location);
				collection.update(new BasicDBObject("_id", object.get("_id")), object);
			}
			System.out.println(" # " + objectIdentifier);
		}
	}

	public static void updateField(DbConnection connection, FieldInfo field, String from, String to) {
		DBCollection collection = connection.getMongoDb().getCollection("objects");
		for (DBObject o : collection.find()) {
			Object value = o.get(field.name());
			if (value instanceof DBObject) {
				@SuppressWarnings("unchecked")
				List<String> values = (List<String>) ((DBObject) value).get("values");
				for (int i = 0; i < values.size(); i++) {
					String v = values.get(i);
					values.set(i, v.replace(from, to));
				}
			} else if (value instanceof String) {
				value = ((String) value).replace(from, to);
			} else {
				continue;
			}
			o.put(field.name(), value);
			collection.update(new BasicDBObject("_id", o.get("_id")), o);
		}
	}
}

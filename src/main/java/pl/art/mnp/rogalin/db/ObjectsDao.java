package pl.art.mnp.rogalin.db;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.ui.field.UiFieldType;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;

public class ObjectsDao {

	protected static final Logger LOG = Logger.getLogger(ObjectsDao.class.getName());

	static final String OBJECTS = "objects";

	private DbConnection dbProvider;

	ObjectsDao(DbConnection dbProvider) {
		this.dbProvider = dbProvider;
	}

	public List<DBObject> getObjectList() {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		return collection.find().sort(new BasicDBObject("_id", 1)).toArray();
	}

	public Collection<DBObject> getFilteredObjectList(String query) {
		final DBObject search = new BasicDBObject("$search", "\"" + query + "\"");
		final DBObject textSearch = new BasicDBObject("$text", search);

		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		List<DBObject> list = collection.find(textSearch).toArray();
		Map<ObjectId, DBObject> result = new TreeMap<ObjectId, DBObject>();
		if (list != null) {
			for (DBObject o : list) {
				ObjectId id = (ObjectId) o.get("_id");
				result.put(id, o);
			}
		}
		return result.values();
	}

	public DBObject getObject(Object id) {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		DBObject ref = new BasicDBObject();
		ref.put("_id", id);
		return collection.findOne(ref);
	}

	public DBObject addObject(Map<FieldInfo, UiFieldType> fields, BasicDBList photos) {
		DBObject object = createDbObjectFromFields(fields, photos);
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.insert(object);
		return object;
	}

	private DBObject createDbObjectFromFields(Map<FieldInfo, UiFieldType> fields, BasicDBList photos) {
		DBObject object = new BasicDBObject();
		for (Entry<FieldInfo, UiFieldType> entry : fields.entrySet()) {
			object.put(entry.getKey().name(), entry.getValue().getDbObject());
		}
		object.put("photos", photos);
		return object;
	}

	public DBObject updateObject(Map<FieldInfo, UiFieldType> fields, BasicDBList photos, ObjectId id) {
		DBObject newObject = createDbObjectFromFields(fields, photos);
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.update(new BasicDBObject("_id", id), newObject);
		return newObject;
	}

	public DBObject updateObject(DBObject dbObject, ObjectId id) {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.update(new BasicDBObject("_id", id), dbObject);
		return dbObject;
	}

	@SuppressWarnings("unchecked")
	public void removeObject(DBObject object) {
		GridFS gridFs = dbProvider.getGridFS();
		for (DBObject photo : (List<DBObject>) object.get("photos")) {
			gridFs.remove(new BasicDBObject("_id", photo.get("file_id")));
		}
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.remove(new BasicDBObject("_id", object.get("_id")));
	}

	@SuppressWarnings("unchecked")
	public List<DbPhoto> getPhotos(DBObject object) {
		List<DbPhoto> photos = new ArrayList<DbPhoto>();
		for (DBObject photo : (List<DBObject>) object.get("photos")) {
			try {
				photos.add(new DbPhoto(photo));
			} catch (FileNotFoundException e) {
				continue;
			}
		}
		return photos;
	}
}

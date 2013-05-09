package pl.art.mnp.rogalin.db;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.types.ObjectId;

import pl.art.mnp.rogalin.ui.field.UiField;
import pl.art.mnp.rogalin.ui.tab.object.photo.DbPhoto;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;

@SuppressWarnings("serial")
public class ObjectsDao implements Serializable {

	protected static final Logger LOG = Logger.getLogger(ObjectsDao.class.getName());

	private static final String OBJECTS = "objects";

	private MongoDbProvider dbProvider;

	ObjectsDao(MongoDbProvider dbProvider) {
		this.dbProvider = dbProvider;
	}

	public List<DBObject> getObjectList() {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		return collection.find().toArray();
	}

	public List<DBObject> getFilteredObjectList(String query) {
		List<DBObject> objects = new ArrayList<DBObject>();
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);

		final DBObject textSearchCommand = new BasicDBObject();
		textSearchCommand.put("text", OBJECTS);
		textSearchCommand.put("search", query);
		textSearchCommand.put("project", new BasicDBObject("_id", 1));
		final CommandResult commandResult = dbProvider.getMongoDb().command(textSearchCommand);
		@SuppressWarnings("unchecked")
		List<DBObject> list = (List<DBObject>) commandResult.get("results");
		LOG.log(Level.WARNING, list.toString());
		for (DBObject o : list) {
			ObjectId id = (ObjectId) ((DBObject) o.get("obj")).get("_id");
			DBObject object = collection.findOne(new BasicDBObject("_id", id));
			objects.add(object);
		}
		return objects;
	}

	public DBObject getObject(Object id) {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		DBObject ref = new BasicDBObject();
		ref.put("_id", id);
		return collection.findOne(ref);
	}

	public void addObject(List<UiField> fields, BasicDBList photos) {
		DBObject object = createDbObjectFromFields(fields, photos);
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.insert(object);
	}

	private DBObject createDbObjectFromFields(List<UiField> fields, BasicDBList photos) {
		DBObject object = new BasicDBObject();
		for (UiField field : fields) {
			object.put(field.getFieldInfo().name(), field.serializeToMongo());
		}
		object.put("photos", photos);
		return object;
	}

	public void updateObject(List<UiField> fields, BasicDBList photos, ObjectId id) {
		DBObject newObject = createDbObjectFromFields(fields, photos);
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.update(new BasicDBObject("_id", id), newObject);
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
				photos.add(new DbPhoto(photo, dbProvider));
			} catch (FileNotFoundException e) {
				continue;
			}
		}
		return photos;
	}
}

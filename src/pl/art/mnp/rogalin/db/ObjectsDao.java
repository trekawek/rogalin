package pl.art.mnp.rogalin.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.art.mnp.rogalin.ui.field.UiField;
import pl.art.mnp.rogalin.ui.tab.object.photo.DbPhoto;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;

@SuppressWarnings("serial")
public class ObjectsDao implements Serializable {

	private static final String OBJECTS = "objects";

	private MongoDbProvider dbProvider;

	ObjectsDao(MongoDbProvider dbProvider) {
		this.dbProvider = dbProvider;
	}

	public List<DBObject> getObjectList() {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		return collection.find().toArray();
	}

	public DBObject getObject(Object id) {
		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		DBObject ref = new BasicDBObject();
		ref.put("_id", id);
		return collection.findOne(ref);
	}

	public void addNewObject(List<UiField> fields, BasicDBList photos) {
		DBObject object = new BasicDBObject();
		for (UiField field : fields) {
			object.put(field.getFieldInfo().name(), field.serializeToMongo());
		}
		object.put("photos", photos);

		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.insert(object);
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
		GridFS gridFs = dbProvider.getGridFS();
		List<DbPhoto> photos = new ArrayList<DbPhoto>();
		for (DBObject photo : (List<DBObject>) object.get("photos")) {
			photos.add(new DbPhoto(photo, gridFs));
		}
		return photos;
	}
}

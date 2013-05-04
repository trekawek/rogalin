package pl.art.mnp.rogalin.db;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.art.mnp.rogalin.ui.field.UiField;
import pl.art.mnp.rogalin.ui.tab.object.UploadedImage;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ObjectsDao {
	private static final Logger LOG = Logger.getLogger(ObjectsDao.class.getName());

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

	public void addNewObject(List<UiField> fields, Collection<UploadedImage> images) {
		DBObject object = new BasicDBObject();
		for (UiField field : fields) {
			object.put(field.getFieldInfo().name(), field.serializeToMongo());
		}
		BasicDBList photos = new BasicDBList();
		for (UploadedImage image : images) {
			try {
				photos.add(image.serializeToMongo(dbProvider.getGridFS()));
			} catch (FileNotFoundException e) {
				LOG.log(Level.WARNING, "Can't save file", e);
			}
		}
		object.put("photos", photos);

		DBCollection collection = dbProvider.getMongoDb().getCollection(OBJECTS);
		collection.insert(object);
	}
}

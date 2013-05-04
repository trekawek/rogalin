package pl.art.mnp.rogalin.db;

import java.util.Arrays;
import java.util.List;

import pl.art.mnp.rogalin.model.Field;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class OptionsDao {
	private final DB mongoDb;

	OptionsDao(DB mongoDb) {
		this.mongoDb = mongoDb;
	}

	@SuppressWarnings("unchecked")
	public List<String> getOptions(Field field) {
		DBCollection collection = mongoDb.getCollection("options");
		BasicDBObject ref = new BasicDBObject();
		ref.put("name", field.name());
		List<String> values = Arrays.asList();
		DBObject result = collection.findOne(ref);
		if (result != null) {
			values = (List<String>) result.get("values");
		}
		return values;
	}

	public void saveOptions(Field field, List<String> options) {
		DBCollection collection = mongoDb.getCollection("options");
		BasicDBObject ref = new BasicDBObject();
		ref.put("name", field.name());

		BasicDBObject update = new BasicDBObject();
		update.put("name", field.name());
		update.put("values", options);
		collection.update(ref, update);
	}
}

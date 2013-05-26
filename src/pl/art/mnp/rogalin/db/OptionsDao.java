package pl.art.mnp.rogalin.db;

import java.util.ArrayList;
import java.util.List;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class OptionsDao {
	private final DbConnection dbProvider;

	OptionsDao(DbConnection dbProvider) {
		this.dbProvider = dbProvider;
	}

	@SuppressWarnings("unchecked")
	public List<String> getOptions(FieldInfo field) {
		DBCollection collection = dbProvider.getMongoDb().getCollection("options");
		BasicDBObject ref = new BasicDBObject();
		ref.put("name", field.name());
		List<String> values = new ArrayList<String>();
		DBObject result = collection.findOne(ref);
		if (result != null) {
			values = (List<String>) result.get("values");
		}
		return values;
	}

	public void saveOptions(FieldInfo field, List<String> options) {
		DBCollection collection = dbProvider.getMongoDb().getCollection("options");
		BasicDBObject ref = new BasicDBObject();
		ref.put("name", field.name());

		BasicDBObject update = new BasicDBObject();
		update.put("name", field.name());
		update.put("values", options);
		if (collection.findOne(ref) != null) {
			collection.update(ref, update);
		} else {
			collection.insert(update);
		}
	}

	public void addOption(FieldInfo field, String option) {
		List<String> options = new ArrayList<String>();
		options.addAll(getOptions(field));
		options.add(option);
		saveOptions(field, options);
	}
}

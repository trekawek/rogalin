package pl.art.mnp.rogalin.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.art.mnp.rogalin.model.FieldInfo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class OptionsDao implements Serializable {
	private final MongoDbProvider dbProvider;

	OptionsDao(MongoDbProvider dbProvider) {
		this.dbProvider = dbProvider;
	}

	@SuppressWarnings("unchecked")
	public List<String> getOptions(FieldInfo field) {
		DBCollection collection = dbProvider.getMongoDb().getCollection("options");
		BasicDBObject ref = new BasicDBObject();
		ref.put("name", field.name());
		List<String> values = Arrays.asList();
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
		collection.update(ref, update);
	}

	public void addOption(FieldInfo field, String option) {
		List<String> options = new ArrayList<String>();
		options.addAll(getOptions(field));
		options.add(option);
		saveOptions(field, options);
	}
}

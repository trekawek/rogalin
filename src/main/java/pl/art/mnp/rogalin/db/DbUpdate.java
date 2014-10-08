package pl.art.mnp.rogalin.db;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DbUpdate {
	private static final Logger LOG = Logger.getLogger(DbUpdate.class.getName());

	private static final int DB_VERSION = 9;

	private DBCollection metadata;

	private DBObject metadataObj;

	private DbConnection connection;

	public DbUpdate(DbConnection connection) {
		this.connection = connection;
		metadata = connection.getMongoDb().getCollection("metadata");
		DBObject obj = metadata.findOne();
		if (obj == null) {
			metadata.save(new BasicDBObject());
			metadataObj = metadata.findOne();
		} else {
			metadataObj = obj;
		}
	}

	public void update() {

		int currentDbVersion = getVersion();
		for (int i = currentDbVersion + 1; i <= DB_VERSION; i++) {
			LOG.log(Level.INFO, "update to " + i);
			if (i == 1) {
				ManagementUtils.changeFieldToMultiselect(connection, FieldInfo.TECHNIQUE);
			}
			if (i == 2) {
				ManagementUtils.changeFieldToMultiselect(connection, FieldInfo.VENEER_TYPE);
			}
			if (i == 3) {
				ManagementUtils.changeFieldToMultiselect(connection, FieldInfo.INTARSIA_TYPE);
			}
			if (i == 4) {
				ManagementUtils.changeFieldToMultiselect(connection, FieldInfo.INTARSIA_TYPE);
			}
			if (i == 5) {
				ManagementUtils.importLocations(connection);
			}
			if (i == 6) {
				DBCollection collection = connection.getMongoDb().getCollection("objects");
				for (DBObject o : collection.find()) {
					String location = (String) o.get(FieldInfo.LOCATION.name());
					if (location != null) {
						location = location.replace("pĂłĹ‚ka", "półka");
						location = location.replace("Ĺ›ciana", "ściana");
						location = location.replace("podĹ‚oga", "podłoga");
						location = location.replace("regaĹ", "regał");
						o.put(FieldInfo.LOCATION.name(), location);
						collection.update(new BasicDBObject("_id", o.get("_id")), o);
					}
				}
			}
			if (i == 7) {
				connection.getOptionsDao().saveOptions(FieldInfo.CONTAINER_NO,
						Arrays.asList("1", "2", "3", "4", "5", "6"));
				connection.getOptionsDao().saveOptions(FieldInfo.CONTAINER_SEGMENT,
						Arrays.asList("a", "b", "c", "d"));
			}
			if (i == 8) {
				ManagementUtils.updateField(connection, FieldInfo.OTHER_DAMAGES, "zarysowania, wgniecenia",
						"zarysowania");
				OptionsDao optionsDao = connection.getOptionsDao();
				List<String> options = optionsDao.getOptions(FieldInfo.OTHER_DAMAGES);
				for (int j = 0; j < options.size(); j++) {
					if ("zarysowania, wgniecenia".equals(options.get(j))) {
						options.set(j, "zarysowania");
						options.add(j + 1, "wgniecenia");
						break;
					}
				}
				optionsDao.saveOptions(FieldInfo.OTHER_DAMAGES, options);
			}
			if (i == 9) {
				ManagementUtils.updateField(connection, FieldInfo.DEPARTMENT, "Malarstwo Obce",
						"Malarstwo Europejskie");
			}

			setVersion(i);
		}
	}

	private int getVersion() {
		if (metadataObj.containsField("version")) {
			return (Integer) metadataObj.get("version");
		} else {
			return 0;
		}
	}

	private void setVersion(int version) {
		metadataObj.put("version", version);
		metadata.update(new BasicDBObject("_id", metadataObj.get("_id")), metadataObj);
	}
}

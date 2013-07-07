package pl.art.mnp.rogalin.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DbUpdate {
	private static final Logger LOG = Logger.getLogger(DbUpdate.class.getName());

	private static final int DB_VERSION = 3;

	private DBCollection metadata;

	private DBObject metadataObj;

	public DbUpdate(DbConnection connection) {
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
				ManagementUtils.changeFieldToMultiselect(FieldInfo.TECHNIQUE);
			}
			if (i == 2) {
				ManagementUtils.changeFieldToMultiselect(FieldInfo.VENEER_TYPE);
			}
			if (i == 3) {
				ManagementUtils.changeFieldToMultiselect(FieldInfo.INTARSIA_TYPE);
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

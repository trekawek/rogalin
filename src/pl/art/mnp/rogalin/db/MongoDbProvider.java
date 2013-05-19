package pl.art.mnp.rogalin.db;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

@SuppressWarnings("serial")
public class MongoDbProvider implements Serializable {

	protected static final Logger LOG = Logger.getLogger(MongoDbProvider.class.getName());

	private transient DB mongoDb;

	private transient MongoClient mongo;

	private transient GridFS gridFs;

	public void connect() throws UnknownHostException {
		LOG.info("Connecting to mongo");
		mongo = new MongoClient();
		mongoDb = mongo.getDB("rogalin");
		gridFs = new GridFS(getMongoDb(), "photos");
	}

	DB getMongoDb() {
		return mongoDb;
	}

	public GridFS getGridFS() {
		return gridFs;
	}

	public OptionsDao getOptionsProvider() {
		return new OptionsDao(this);
	}

	public ObjectsDao getObjectsProvider() {
		return new ObjectsDao(this);
	}

	public void close() {
		LOG.info("Closing connection");
		if (mongo != null) {
			mongo.close();
		}
		mongo = null;
		mongoDb = null;
		gridFs = null;
	}
}
package pl.art.mnp.rogalin.db;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

public class DbConnection {

	private static final Logger LOG = Logger.getLogger(DbConnection.class.getName());

	private static DbConnection instance;

	private DB mongoDb;

	private MongoClient mongo;

	private GridFS gridFs;

	private DbConnection() {
		LOG.info("Connecting to mongo");
		try {
			mongo = new MongoClient();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		mongoDb = mongo.getDB("rogalin");
		gridFs = new GridFS(getMongoDb(), "photos");
	}

	public static DbConnection getInstance() {
		if (instance == null) {
			synchronized (DbConnection.class) {
				if (instance == null) {
					instance = new DbConnection();
				}
			}
		}
		return instance;
	}

	DB getMongoDb() {
		return mongoDb;
	}

	public GridFS getGridFS() {
		return gridFs;
	}

	public OptionsDao getOptionsDao() {
		return new OptionsDao(this);
	}

	public ObjectsDao getObjectsDao() {
		return new ObjectsDao(this);
	}

	/*public void close() {
		LOG.info("Closing connection");
		if (mongo != null) {
			mongo.close();
		}
		mongo = null;
		mongoDb = null;
		gridFs = null;
	}*/
}
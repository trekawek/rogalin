package pl.art.mnp.rogalin.db;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

@SuppressWarnings("serial")
public class MongoDbProvider implements Serializable {

	private transient DB mongoDb;

	public MongoDbProvider() throws UnknownHostException {
		initMongoDb();
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initMongoDb();
	}

	private void initMongoDb() throws UnknownHostException {
		MongoClient mongo = new MongoClient();
		mongoDb = mongo.getDB("rogalin");
	}

	DB getMongoDb() {
		return mongoDb;
	}

	GridFS getGridFS() {
		return new GridFS(mongoDb, "photos");
	}

	public OptionsDao getOptionsProvider() {
		return new OptionsDao(this);
	}

	public ObjectsDao getObjectsProvider() {
		return new ObjectsDao(this);
	}
}

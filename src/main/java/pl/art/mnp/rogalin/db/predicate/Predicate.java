package pl.art.mnp.rogalin.db.predicate;

import java.io.Serializable;

import com.mongodb.DBObject;

public interface Predicate extends Serializable {
	boolean matches(DBObject dbObject);
}

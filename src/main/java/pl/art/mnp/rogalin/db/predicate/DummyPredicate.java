package pl.art.mnp.rogalin.db.predicate;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class DummyPredicate implements Predicate {

	@Override
	public boolean matches(DBObject dbObject) {
		return true;
	}

}

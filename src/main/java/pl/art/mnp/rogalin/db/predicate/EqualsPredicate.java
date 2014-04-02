package pl.art.mnp.rogalin.db.predicate;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class EqualsPredicate implements Predicate {

	private final String subject;

	private final String fieldName;

	public EqualsPredicate(String subject, String fieldName) {
		this.subject = subject;
		this.fieldName = fieldName;
	}

	@Override
	public boolean matches(DBObject dbObject) {
		return subject.equalsIgnoreCase((String) dbObject.get(fieldName));
	}
}

package pl.art.mnp.rogalin.db.predicate;

import org.apache.commons.lang.StringUtils;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class ContainsPredicate implements Predicate {

	private final String subject;

	private final String fieldName;

	public ContainsPredicate(String subject, String fieldName) {
		this.subject = subject;
		this.fieldName = fieldName;
	}

	@Override
	public boolean matches(DBObject dbObject) {
		return StringUtils.containsIgnoreCase((String) dbObject.get(fieldName), subject);
	}

}

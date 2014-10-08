package pl.art.mnp.rogalin.db.predicate;

import org.apache.commons.lang.StringUtils;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class IsEmptyPredicate implements Predicate {

	private final String fieldName;

	public IsEmptyPredicate(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public boolean matches(DBObject dbObject) {
		return StringUtils.isEmpty((String) dbObject.get(fieldName));
	}
}

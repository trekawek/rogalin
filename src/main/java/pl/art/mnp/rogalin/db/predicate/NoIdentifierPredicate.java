package pl.art.mnp.rogalin.db.predicate;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;

import com.mongodb.DBObject;

public class NoIdentifierPredicate implements Predicate {

	private static final long serialVersionUID = 2728677633830394016L;

	private static final String[] VALID_PREFIX = new String[] { "Mo", "Mp", "P", "Rd", "Dep", "Gr", "I", "Rog" };

	@Override
	public boolean matches(DBObject dbObject) {
		final String identifier = StringUtils.trimToEmpty((String) dbObject.get(FieldInfo.IDENTIFIER.name()));
		for (final String prefix : VALID_PREFIX) {
			if (identifier.startsWith(prefix)) {
				return false;
			}
		}
		return true;
	}

}

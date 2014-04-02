package pl.art.mnp.rogalin.db.predicate;

import java.util.Collection;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class MultiSelectPredicate implements Predicate {

	private final Collection<String> subset;

	private final String fieldName;

	public MultiSelectPredicate(Collection<String> subset, String fieldName) {
		this.subset = subset;
		this.fieldName = fieldName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean matches(DBObject dbObject) {
		if (subset.isEmpty()) {
			return true;
		}

		DBObject field = (DBObject) dbObject.get(fieldName);
		if (field == null) {
			return false;
		}
		Collection<String> values = (Collection<String>) field.get("values");
		for (String s : subset) {
			if (values.contains(s)) {
				return true;
			}
		}
		return false;
	}
}

package pl.art.mnp.rogalin.field.creationdate;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.Predicate;
import pl.art.mnp.rogalin.field.DateOfCreationFieldType;

import com.mongodb.DBObject;

@SuppressWarnings("serial")
public class CenturyPredicate implements Predicate {

	private final CenturyType centuryType;

	private final int century;

	private final FieldInfo fieldInfo;

	private final DateOfCreationFieldType dateOfCreationFieldType;

	private final Range filterRange;

	public CenturyPredicate(CenturyType centuryType, int century, FieldInfo fieldInfo) {
		this.centuryType = centuryType;
		this.century = century;
		this.fieldInfo = fieldInfo;
		this.dateOfCreationFieldType = new DateOfCreationFieldType(fieldInfo);
		this.filterRange = centuryType.getRange().add((century - 1) * 100);
	}

	@Override
	public boolean matches(DBObject dbObject) {
		DBObject field = (DBObject) dbObject.get(fieldInfo.name());
		if (field == null) {
			return false;
		}
		String dateTypeName = (String) field.get("dateType");
		if (StringUtils.isEmpty(dateTypeName)) {
			return false;
		}
		DateType dateType = DateType.valueOf(dateTypeName);
		if (dateType == DateType.CENTURY) {
			DBObject value = (DBObject) field.get("date");
			CenturyType dbCenturyType = CenturyType.valueOf((String) value.get("centuryType"));
			int dbCentury = (Integer) value.get("century");
			return dbCentury == century && dbCenturyType == centuryType;
		}
		Range dbRange = dateOfCreationFieldType.getRange(dbObject);
		return filterRange.contains(dbRange);
	}

}
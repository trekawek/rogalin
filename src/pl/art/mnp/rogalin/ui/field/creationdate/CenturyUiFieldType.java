package pl.art.mnp.rogalin.ui.field.creationdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.Predicate;
import pl.art.mnp.rogalin.field.creationdate.Century;
import pl.art.mnp.rogalin.field.creationdate.CenturyPredicate;
import pl.art.mnp.rogalin.field.creationdate.CenturyType;
import pl.art.mnp.rogalin.ui.field.AbstractUiFieldType;

@SuppressWarnings("serial")
public class CenturyUiFieldType extends AbstractUiFieldType {

	private final HorizontalLayout centuryLayout;

	private final NativeSelect centuryType;

	private final NativeSelect century;

	public CenturyUiFieldType(FieldInfo fieldInfo, boolean searchField) {
		super(fieldInfo);
		centuryLayout = new HorizontalLayout();
		if (searchField) {
			centuryLayout.setCaption("Data powstania");
		}

		centuryType = new NativeSelect(null, Arrays.asList(CenturyType.values()));
		centuryType.setNullSelectionAllowed(false);
		centuryType.setNewItemsAllowed(false);
		centuryType.select(CenturyType.NONE);
		if (!searchField) {
			centuryType.setRequired(true);
		}
		centuryLayout.addComponent(centuryType);

		century = new NativeSelect(null, getCenturyRange());
		century.setNullSelectionAllowed(false);
		century.setNewItemsAllowed(false);
		if (!searchField) {
			century.setRequired(true);
		}
		centuryLayout.addComponents(century);

	}

	@Override
	public Component getComponent() {
		return centuryLayout;
	}

	@Override
	public Object getDbObject() {
		DBObject o = new BasicDBObject();
		o.put("centuryType", ((CenturyType) centuryType.getValue()).name());
		o.put("century", ((Century) century.getValue()).getCentury());
		return o;
	}

	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		DBObject value = (DBObject) object.get("date");
		if (value == null) {
			return;
		}
		centuryType.setValue(CenturyType.valueOf((String) value.get("centuryType")));
		century.setValue(new Century((Integer) value.get("century")));
	}

	@Override
	public void validate() {
		centuryType.validate();
		century.validate();
	}

	@Override
	public void clear() {
		centuryType.select(CenturyType.NONE);
		century.setValue(null);
	}

	@Override
	public Predicate getPredicate() {
		CenturyType type = (CenturyType) centuryType.getValue();
		Century cent = (Century) century.getValue();
		if (type == null || cent == null) {
			return DUMMY_PREDICATE;
		}
		return new CenturyPredicate(type, cent.getCentury(), fieldInfo);
	}

	private static List<Century> getCenturyRange() {
		List<Century> range = new ArrayList<Century>();
		for (int i = 13; i <= 21; i++) {
			range.add(new Century(i));
		}
		return range;
	}
}

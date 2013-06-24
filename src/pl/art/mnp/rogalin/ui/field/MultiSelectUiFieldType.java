package pl.art.mnp.rogalin.ui.field;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.MultiSelectPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class MultiSelectUiFieldType extends AbstractUiFieldType {

	private static final long serialVersionUID = 7251529681447360987L;

	private final VerticalLayout layout;

	private final OptionGroup optionGroup;

	private final CheckBox otherBox;

	private final TextField other;

	@SuppressWarnings("serial")
	public MultiSelectUiFieldType(FieldInfo field, List<String> options, boolean search, boolean showOther) {
		super(field);
		layout = new VerticalLayout();
		layout.setCaption(field.toString());

		optionGroup = new OptionGroup(null, options);
		optionGroup.setNullSelectionAllowed(true);
		optionGroup.setNewItemsAllowed(false);
		optionGroup.setMultiSelect(true);
		optionGroup.setImmediate(true);
		if (!search) {
			optionGroup.setRequired(field.isRequired());
		}
		optionGroup.setValidationVisible(false);
		optionGroup.setRequiredError(EMPTY_FIELD_ERROR);
		layout.addComponent(optionGroup);

		other = new TextField();
		other.setEnabled(false);
		other.setValidationVisible(false);
		other.setRequiredError(EMPTY_FIELD_ERROR);
		otherBox = new CheckBox("inne: ");
		otherBox.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				other.setEnabled(otherBox.getValue());
			}
		});

		HorizontalLayout otherLayout = new HorizontalLayout();
		otherLayout.setSpacing(true);
		otherLayout.addComponent(otherBox);
		otherLayout.addComponent(other);
		if (showOther) {
			layout.addComponent(otherLayout);
		}
	}

	@Override
	public void addOnChangeListener(ValueChangeListener listener) {
		optionGroup.addValueChangeListener(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getDbObject() {
		DBObject o = new BasicDBObject();
		BasicDBList values = new BasicDBList();
		values.addAll((Set<String>) optionGroup.getValue());
		o.put("values", values);
		if (otherBox.getValue()) {
			o.put("other", other.getValue());
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFromDbObject(DBObject object) {
		if (object == null) {
			return;
		}
		DBObject o = (DBObject) object.get(fieldInfo.name());
		if (o == null) {
			return;
		}
		Collection<String> values = (Collection<String>) o.get("values");
		if (values == null) {
			return;
		}
		String otherValue = (String) o.get("other");
		optionGroup.setValue(values);
		if (otherValue != null) {
			otherBox.setValue(true);
			other.setValue(otherValue);
		}
	}

	@Override
	public void validate() {
		optionGroup.validate();
		if (otherBox.getValue()) {
			other.validate();
		}
	}

	@Override
	public void clear() {
		optionGroup.setValue(Collections.emptySet());
		otherBox.setValue(false);
		other.setValue("");
	}

	@Override
	public Component getComponent() {
		return layout;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Predicate getPredicate() {
		if (optionGroup.getValue() == null) {
			return DUMMY_PREDICATE;
		}
		return new MultiSelectPredicate((Collection<String>) optionGroup.getValue(), fieldInfo.name());
	}

	@Override
	public void setEnabled(boolean visible) {
		layout.setVisible(visible);
	}
}

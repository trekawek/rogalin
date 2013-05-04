package pl.art.mnp.rogalin.ui.field;

import java.util.List;
import java.util.Set;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.Field;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MultiSelectUiField extends AbstractUiField {

	private final VerticalLayout layout;

	private final OptionGroup optionGroup;

	private final CheckBox otherBox;

	private final TextField other;

	public MultiSelectUiField(Field field, MongoDbProvider dbProvider) {
		super(field, dbProvider);
		layout = new VerticalLayout();
		layout.setCaption(field.toString());

		List<String> options = dbProvider.getOptionsProvider().getOptions(field);
		optionGroup = new OptionGroup(null, options);
		optionGroup.setNullSelectionAllowed(true);
		optionGroup.setNewItemsAllowed(false);
		optionGroup.setMultiSelect(true);
		optionGroup.setRequired(field.isRequired());
		optionGroup.setValidationVisible(true);
		optionGroup.setRequiredError(EMPTY_FIELD_ERROR);
		layout.addComponent(optionGroup);

		other = new TextField();
		other.setEnabled(false);
		other.setValidationVisible(true);
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
		layout.addComponent(otherLayout);
	}

	@Override
	public VerticalLayout getComponent() {
		return layout;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object serializeToMongo() {
		DBObject o = new BasicDBObject();
		BasicDBList values = new BasicDBList();
		values.addAll((Set<String>) optionGroup.getValue());
		o.put("values", values);
		if (otherBox.getValue()) {
			o.put("other", other.getValue());
		}
		return o;
	}

	@Override
	public void validate() {
		optionGroup.validate();
		if (otherBox.getValue()) {
			other.validate();
		}
	}
}

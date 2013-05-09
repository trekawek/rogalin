package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.FieldInfo;

import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class SelectUiField extends AbstractUiField {

	private final ComboBox comboBox;

	public SelectUiField(FieldInfo field, MongoDbProvider dbProvider) {
		super(field, dbProvider);
		List<String> options = dbProvider.getOptionsProvider().getOptions(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNullSelectionAllowed(false);
		comboBox.setNewItemsAllowed(false);
		comboBox.setTextInputAllowed(false);
		comboBox.setPageLength(0);
		comboBox.setRequired(field.isRequired());
		comboBox.setValidationVisible(true);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public ComboBox getComponent() {
		return comboBox;
	}
}

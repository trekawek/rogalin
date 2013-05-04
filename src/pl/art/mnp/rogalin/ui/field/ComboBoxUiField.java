package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.model.Field;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class ComboBoxUiField extends AbstractUiField {

	private final ComboBox comboBox;

	public ComboBoxUiField(Field field, MongoDbProvider dbProvider) {
		super(field, dbProvider);
		List<String> options = dbProvider.getOptionsProvider().getOptions(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNullSelectionAllowed(false);
		comboBox.setNewItemsAllowed(true);
		comboBox.setTextInputAllowed(true);
		comboBox.setPageLength(0);
		comboBox.setRequired(field.isRequired());
		comboBox.setValidationVisible(true);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
		comboBox.setNewItemHandler(new AbstractSelect.NewItemHandler() {
			@Override
			public void addNewItem(String caption) {
				comboBox.addItem(caption);
			}
		});
	}

	@Override
	public ComboBox getComponent() {
		return comboBox;
	}
}

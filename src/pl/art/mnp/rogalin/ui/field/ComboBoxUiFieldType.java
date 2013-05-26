package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ComboBoxUiFieldType extends AbstractUiFieldType {

	private final ComboBox comboBox;

	public ComboBoxUiFieldType(final FieldInfo field, List<String> options, boolean search) {
		super(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNullSelectionAllowed(false);
		comboBox.setNewItemsAllowed(true);
		comboBox.setImmediate(true);
		comboBox.setPageLength(0);
		if (!search) {
			comboBox.setRequired(field.isRequired());
		}
		comboBox.setValidationVisible(true);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
		comboBox.setNewItemHandler(new AbstractSelect.NewItemHandler() {
			@Override
			public void addNewItem(String caption) {
				LOG.info("New option: " + caption);
				comboBox.addItem(caption);
				DbConnection.getInstance().getOptionsDao().addOption(field, caption);
				comboBox.select(caption);
			}
		});
	}

	@Override
	public Component getComponent() {
		return comboBox;
	}
}

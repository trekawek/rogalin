package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class ComboBoxUiFieldType extends AbstractUiFieldType {

	private static final long serialVersionUID = -1440161782407422943L;

	private final ComboBox comboBox;

	public ComboBoxUiFieldType(final FieldInfo field, List<String> options, boolean search) {
		super(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNewItemsAllowed(true);
		comboBox.setImmediate(true);
		comboBox.setPageLength(10);
		if (!search) {
			comboBox.setRequired(field.isRequired());
		}
		if (!search && field.isRequired()) {
			comboBox.setNullSelectionAllowed(false);
		}
		comboBox.setValidationVisible(false);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
		comboBox.setNewItemHandler(new AbstractSelect.NewItemHandler() {
			private static final long serialVersionUID = 7266842433719567514L;

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

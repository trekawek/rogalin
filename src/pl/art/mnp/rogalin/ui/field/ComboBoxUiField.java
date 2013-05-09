package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import pl.art.mnp.rogalin.db.MongoDbProvider;
import pl.art.mnp.rogalin.db.OptionsDao;
import pl.art.mnp.rogalin.model.FieldInfo;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class ComboBoxUiField extends AbstractUiField {

	private final ComboBox comboBox;

	private final OptionsDao optionsDao;

	public ComboBoxUiField(final FieldInfo field, MongoDbProvider dbProvider) {
		super(field, dbProvider);
		this.optionsDao = dbProvider.getOptionsProvider();
		List<String> options = dbProvider.getOptionsProvider().getOptions(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNullSelectionAllowed(false);
		comboBox.setNewItemsAllowed(true);
		comboBox.setImmediate(true);
		comboBox.setPageLength(0);
		comboBox.setRequired(field.isRequired());
		comboBox.setValidationVisible(true);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
		comboBox.setNewItemHandler(new AbstractSelect.NewItemHandler() {
			@Override
			public void addNewItem(String caption) {
				LOG.info("New option: " + caption);
				comboBox.addItem(caption);
				optionsDao.addOption(field, caption);
				comboBox.select(caption);
			}
		});
	}

	@Override
	public ComboBox getComponent() {
		return comboBox;
	}
}

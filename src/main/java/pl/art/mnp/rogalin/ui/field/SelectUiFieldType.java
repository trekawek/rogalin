package pl.art.mnp.rogalin.ui.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.EqualsPredicate;
import pl.art.mnp.rogalin.db.predicate.IsEmptyPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class SelectUiFieldType extends AbstractUiFieldType {

	private static final String EMPTY = "<nie wybrano>";

	private final ComboBox comboBox;

	private final String defaultOption;

	public SelectUiFieldType(FieldInfo field, List<String> options, boolean search) {
		this(field, options, search, false, false);
	}

	public SelectUiFieldType(FieldInfo field, List<String> options, boolean search, boolean selectFirstItem,
			boolean emptyAllowed) {
		super(field);
		final List<String> selectOptions;
		if (emptyAllowed && search) {
			selectOptions = new ArrayList<String>();
			selectOptions.add("<nie wybrano>");
			selectOptions.addAll(options);
		} else {
			selectOptions = options;
		}

		comboBox = new ComboBox(null, selectOptions);
		comboBox.setCaption(field.toString());
		comboBox.setNewItemsAllowed(false);
		comboBox.setTextInputAllowed(false);
		comboBox.setPageLength(10);
		if (search) {
			comboBox.setNullSelectionAllowed(true);
		} else if (!emptyAllowed) {
			comboBox.setNullSelectionAllowed(false);
			comboBox.setRequired(field.isRequired());
		}
		comboBox.setValidationVisible(false);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
		if (selectFirstItem && !options.isEmpty()) {
			defaultOption = options.get(0);
		} else {
			defaultOption = null;
		}
		clear();
	}

	@Override
	public Component getComponent() {
		return comboBox;
	}

	@Override
	public Predicate getPredicate() {
		String value = (String) comboBox.getValue();
		if (StringUtils.isEmpty(value)) {
			return DUMMY_PREDICATE;
		} else if (EMPTY.equals(value)) {
			return new IsEmptyPredicate(fieldInfo.name());
		} else {
			return new EqualsPredicate(value, fieldInfo.name());
		}
	}

	@Override
	public void clear() {
		comboBox.setValue(defaultOption);
	}
}

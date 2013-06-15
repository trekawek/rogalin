package pl.art.mnp.rogalin.ui.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.EqualsPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

public class SelectUiFieldType extends AbstractUiFieldType {

	private static final long serialVersionUID = -2001979428978218869L;

	private final ComboBox comboBox;

	public SelectUiFieldType(FieldInfo field, List<String> options, boolean search) {
		super(field);
		comboBox = new ComboBox(null, options);
		comboBox.setCaption(field.toString());
		comboBox.setNewItemsAllowed(false);
		comboBox.setTextInputAllowed(false);
		comboBox.setPageLength(10);
		if (search) {
			comboBox.setNullSelectionAllowed(true);
		} else {
			comboBox.setNullSelectionAllowed(false);
			comboBox.setRequired(field.isRequired());
		}
		comboBox.setValidationVisible(true);
		comboBox.setRequiredError(EMPTY_FIELD_ERROR);
	}

	@Override
	public Component getComponent() {
		return comboBox;
	}

	@Override
	public Predicate getPredicate() {
		if (StringUtils.isEmpty((String) comboBox.getValue())) {
			return DUMMY_PREDICATE;
		}
		return new EqualsPredicate((String) comboBox.getValue(), fieldInfo.name());
	}
}

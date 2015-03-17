package pl.art.mnp.rogalin.ui.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.art.mnp.rogalin.TabsController.PredicateListener;
import pl.art.mnp.rogalin.db.DbConnection;
import pl.art.mnp.rogalin.db.FieldInfo;
import pl.art.mnp.rogalin.db.predicate.DummyPredicate;
import pl.art.mnp.rogalin.db.predicate.IsEmptyPredicate;
import pl.art.mnp.rogalin.db.predicate.Predicate;
import pl.art.mnp.rogalin.field.FieldType;
import pl.art.mnp.rogalin.ui.field.UiFieldType;
import pl.art.mnp.rogalin.ui.photo.DbPhoto;
import pl.art.mnp.rogalin.ui.photo.PhotoType;
import pl.art.mnp.rogalin.ui.tab.ObjectList.ShowAllListener;

import com.mongodb.DBObject;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class SearchTab extends VerticalLayout implements ShowAllListener {

	private static final long serialVersionUID = -7257979675567926412L;

	private final Collection<UiFieldType> fields = new ArrayList<UiFieldType>();

	private final PredicateListener predicateListener;

	private final CheckBox noHistoricPhotoCheckbox;

	private final CheckBox noCurrentPhotoCheckbox;

	private CheckBox noIdentifier;

	public SearchTab(PredicateListener predicateListener) {
		super();
		this.predicateListener = predicateListener;
		this.setSpacing(true);
		this.setMargin(true);

		Button clear = new Button("Wyczyść formularz");
		clear.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -341743033021461309L;

			@Override
			public void buttonClick(ClickEvent event) {
				clear();
			}
		});

		Button search = new Button("Wyszukaj obiekty");
		search.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 181424311297491141L;

			@Override
			public void buttonClick(ClickEvent event) {
				showObjects();
			}
		});
		addComponent(new HorizontalLayout(search, clear));

		GridLayout columns = new GridLayout(2, 1);
		columns.setSpacing(true);
		FormLayout leftColumn = new FormLayout();
		FormLayout rightColumn = new FormLayout();
		FormLayout belowColumns = new FormLayout();
		columns.addComponent(leftColumn, 0, 0);
		columns.addComponent(rightColumn, 1, 0);
		addComponent(columns);
		addComponent(belowColumns);

		int i = 0;
		Layout container = null;
		for (FieldInfo f : FieldInfo.values()) {
			if (!f.isSearchable()) {
				continue;
			}
			FieldType fieldType = f.getFieldType();
			UiFieldType formField = fieldType.getSearchField();
			fields.add(formField);

			if (i <= 21) {
				container = leftColumn;
				i++;
			} else {
				container = rightColumn;
			}
			container.addComponent(formField.getComponent());

			if (f == FieldInfo.IDENTIFIER) {
				noIdentifier = new CheckBox("Obiekty bez numeru inwentarzowego");
				container.addComponent(noIdentifier);
			}
		}
		noCurrentPhotoCheckbox = new CheckBox("Obiekty bez fotografii bieżącej");
		noHistoricPhotoCheckbox = new CheckBox("Obiekty bez fotografii archiwalnej");
		container.addComponents(noCurrentPhotoCheckbox, noHistoricPhotoCheckbox);
	}

	private void showObjects() {
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (UiFieldType field : fields) {
			Predicate p = field.getPredicate();
			if (p instanceof DummyPredicate) {
				continue;
			}
			predicates.add(field.getPredicate());
		}
		if (noCurrentPhotoCheckbox.getValue()) {
			predicates.add(new NoPhotoPredicate(PhotoType.CURRENT));
		}
		if (noHistoricPhotoCheckbox.getValue()) {
			predicates.add(new NoPhotoPredicate(PhotoType.HISTORIC));
		}
		if (noIdentifier.getValue()) {
			predicates.add(new IsEmptyPredicate(FieldInfo.IDENTIFIER.name()));
		}
		predicateListener.gotPredicates(predicates);
	}

	private void clear() {
		noCurrentPhotoCheckbox.setValue(false);
		noHistoricPhotoCheckbox.setValue(false);
		noIdentifier.setValue(false);
		for (UiFieldType field : fields) {
			field.clear();
		}
	}

	@Override
	public void showAll() {
		clear();
	}

	private static class NoPhotoPredicate implements Predicate {
		private static final long serialVersionUID = 8377982253675381734L;

		private final PhotoType type;

		private NoPhotoPredicate(PhotoType type) {
			this.type = type;
		}

		@Override
		public boolean matches(DBObject dbObject) {
			List<DbPhoto> list = DbConnection.getInstance().getObjectsDao().getPhotos(dbObject);
			for (DbPhoto p : list) {
				if (p.getType() == type) {
					return false;
				}
			}
			return true;
		}
	}
}

package pl.art.mnp.rogalin.ui.print;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class ObjectRenderer {
	private final StringBuilder builder;

	private final DBObject dbObject;

	public ObjectRenderer(DBObject dbObject) {
		this.builder = new StringBuilder();
		this.dbObject = dbObject;
		render();
	}

	private void render() {
		builder.append("<h2 class=\"rendered_object_title\">");
		builder.append(FieldInfo.IDENTIFIER.getFieldType().getValue(dbObject));
		builder.append(", ");
		builder.append(FieldInfo.NAME.getFieldType().getValue(dbObject));
		builder.append("</h2>");
		builder.append("<table class=\"rendered_object\">");
		newRow();
		renderCell(FieldInfo.EVALUATION_DATE, 3);
		closeRow();
		newRow();
		renderCell(FieldInfo.AUTHOR, 1);
		renderCell(FieldInfo.DATE_OF_CREATION, 1);
		renderCell(FieldInfo.TYPE, 1);
		closeRow();
		newRow();
		int veenerVisible = isVisible(FieldInfo.VENEER_TYPE) ? 1 : 0;
		int intarsiaVisible = isVisible(FieldInfo.INTARSIA_TYPE) ? 1 : 0;
		renderCell(FieldInfo.TECHNIQUE, 3 - veenerVisible - intarsiaVisible);
		if (veenerVisible == 1) {
			renderCell(FieldInfo.VENEER_TYPE, 1);
		}
		if (intarsiaVisible == 1) {
			renderCell(FieldInfo.INTARSIA_TYPE, 1);
		}
		closeRow();
		newRow();
		if (isVisible(FieldInfo.WOOD_TYPE)) {
			renderCell(FieldInfo.CARRIER, 2);
			renderCell(FieldInfo.WOOD_TYPE, 1);
		} else {
			renderCell(FieldInfo.CARRIER, 3);
		}
		closeRow();
		newRow();
		renderCell(FieldInfo.HEIGHT, 1);
		renderCell(FieldInfo.WIDTH, 1);
		renderCell(FieldInfo.DEPTH, 1);
		closeRow();
		newRow();
		renderCell(FieldInfo.HOME, 1, "St. miejsce");
		renderCell(FieldInfo.LOCATION, 2);
		closeRow();
		newRow();
		renderContainerNo();
		renderCell(FieldInfo.PARTS_NO, 1, "El. składowe");
		renderCell(FieldInfo.PARTS_IN_PACKAGE, 1, "El. po spakowaniu");
		closeRow();

		if (dbObject.containsField("fragments")) {
			DBObject fragments = (DBObject) dbObject.get("fragments");
			BasicDBList list = (BasicDBList) fragments.get("items");
			for (Object rawObject : list) {
				DBObject o = (DBObject) rawObject;
				renderFragment(o);
			}
		}

		newRow();
		renderCell(FieldInfo.DEPARTMENT, 1);
		renderCell(FieldInfo.CONDITION, 1);
		renderCell(FieldInfo.COMPLETENESS, 1);
		closeRow();
		newRow();
		renderCell(FieldInfo.ELEMENT_ABSENCE, 3);
		closeRow();
		newRow();
		renderCell(FieldInfo.CONDITION_DESC, 3);
		closeRow();
		newRow();
		renderCell(FieldInfo.SUPPORT_DAMAGES, 3);
		closeRow();
		newRow();
		renderCell(FieldInfo.OTHER_DAMAGES, 3);
		closeRow();
		newRow();
		renderCell(FieldInfo.DESC, 3);
		closeRow();

		builder.append("</table>");
	}

	private void renderFragment(DBObject o) {
		newRow();
		builder.append("<td colspan=\"1\"><b>Kontener: </b>");
		builder.append(o.get(FieldInfo.CONTAINER_NO.name()));
		builder.append(o.get(FieldInfo.CONTAINER_SEGMENT.name()));
		builder.append(" - ");
		builder.append(o.get("name"));
		builder.append("</td>");
		closeRow();
	}

	private void newRow() {
		builder.append("<tr>");
	}

	private void closeRow() {
		builder.append("</tr>");
	}

	private void renderCell(FieldInfo field, int span) {
		renderCell(field, span, field.toString());
	}

	private void renderCell(FieldInfo field, int span, String title) {
		builder.append("<td colspan=\"").append(span).append("\"");
		builder.append(" class=\"").append(field.name().toLowerCase()).append("\">");
		renderField(field, title);
		builder.append("</td>");
	}

	private void renderField(FieldInfo field, String title) {
		builder.append("<b>");
		builder.append(title).append(": </b>");
		String value = field.getFieldType().getValue(dbObject);
		if (StringUtils.isEmpty(value)) {
			builder.append("-");
		} else {
			builder.append(value);
		}
	}

	private void renderContainerNo() {
		builder.append("<td class=\"container_no\"><b>Kontener: </b>");
		String containerNo = FieldInfo.CONTAINER_NO.getFieldType().getValue(dbObject);
		String containerSegment = FieldInfo.CONTAINER_SEGMENT.getFieldType().getValue(dbObject);
		if (StringUtils.isBlank(containerNo) && StringUtils.isBlank(containerSegment)) {
			builder.append("-");
		} else {
			builder.append(StringUtils.defaultString(containerNo));
			builder.append(StringUtils.defaultString(containerSegment));
		}
		builder.append("</td>");
	}

	@Override
	public String toString() {
		return builder.toString();
	}

	private boolean isVisible(FieldInfo field) {
		if (field.getDependsOn() == null) {
			return true;
		}
		return field.isVisible(dbObject.get(field.getDependsOn().name()));
	}
}

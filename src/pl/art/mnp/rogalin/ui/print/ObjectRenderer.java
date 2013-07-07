package pl.art.mnp.rogalin.ui.print;

import org.apache.commons.lang.StringUtils;

import pl.art.mnp.rogalin.db.FieldInfo;

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
		renderCell(FieldInfo.TECHNIQUE, 1);
		renderCell(FieldInfo.VENEER_TYPE, 1);
		renderCell(FieldInfo.INTARSIA_TYPE, 1);
		closeRow();
		newRow();
		renderCell(FieldInfo.CARRIER, 2);
		renderCell(FieldInfo.WOOD_TYPE, 1);
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
		renderCell(FieldInfo.TEMPORARY_HOME, 1, "Tymcz. miejsce");
		renderCell(FieldInfo.PARTS_NO, 1, "El. składowe");
		renderCell(FieldInfo.PARTS_IN_PACKAGE, 1, "El. po spakowaniu");
		closeRow();
		newRow();
		renderCell(FieldInfo.CONDITION, 1);
		renderCell(FieldInfo.COMPLETENESS, 2);
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

	@Override
	public String toString() {
		return builder.toString();
	}
}

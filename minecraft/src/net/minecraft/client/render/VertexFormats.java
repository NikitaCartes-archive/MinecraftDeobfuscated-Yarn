package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormatElement field_1587 = new VertexFormatElement(0, VertexFormatElement.Format.field_1623, VertexFormatElement.Type.field_1633, 3);
	public static final VertexFormatElement field_1581 = new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement field_1591 = new VertexFormatElement(0, VertexFormatElement.Format.field_1623, VertexFormatElement.Type.field_1636, 2);
	public static final VertexFormatElement field_1583 = new VertexFormatElement(1, VertexFormatElement.Format.field_1625, VertexFormatElement.Type.field_1636, 2);
	public static final VertexFormatElement field_1579 = new VertexFormatElement(0, VertexFormatElement.Format.field_1621, VertexFormatElement.Type.field_1635, 3);
	public static final VertexFormatElement field_1578 = new VertexFormatElement(0, VertexFormatElement.Format.field_1621, VertexFormatElement.Type.field_1629, 1);
	public static final VertexFormat field_1582 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1581)
		.method_1361(field_1591)
		.method_1361(field_1583);
	public static final VertexFormat field_1590 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1581)
		.method_1361(field_1591)
		.method_1361(field_1579)
		.method_1361(field_1578);
	public static final VertexFormat field_1580 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1591)
		.method_1361(field_1579)
		.method_1361(field_1578);
	public static final VertexFormat field_1584 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1591)
		.method_1361(field_1581)
		.method_1361(field_1583);
	public static final VertexFormat field_1592 = new VertexFormat().method_1361(field_1587);
	public static final VertexFormat field_1576 = new VertexFormat().method_1361(field_1587).method_1361(field_1581);
	public static final VertexFormat field_1585 = new VertexFormat().method_1361(field_1587).method_1361(field_1591);
	public static final VertexFormat field_1588 = new VertexFormat().method_1361(field_1587).method_1361(field_1579).method_1361(field_1578);
	public static final VertexFormat field_1575 = new VertexFormat().method_1361(field_1587).method_1361(field_1591).method_1361(field_1581);
	public static final VertexFormat field_1589 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1591)
		.method_1361(field_1579)
		.method_1361(field_1578);
	public static final VertexFormat field_1586 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1591)
		.method_1361(field_1583)
		.method_1361(field_1581);
	public static final VertexFormat field_1577 = new VertexFormat()
		.method_1361(field_1587)
		.method_1361(field_1591)
		.method_1361(field_1581)
		.method_1361(field_1579)
		.method_1361(field_1578);
}

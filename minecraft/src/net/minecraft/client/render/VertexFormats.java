package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(
		0, VertexFormatElement.Format.field_1623, VertexFormatElement.Type.field_1633, 3
	);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement UV_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.field_1623, VertexFormatElement.Type.field_1636, 2);
	public static final VertexFormatElement LMAP_ELEMENT = new VertexFormatElement(
		1, VertexFormatElement.Format.field_1625, VertexFormatElement.Type.field_1636, 2
	);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(
		0, VertexFormatElement.Format.field_1621, VertexFormatElement.Type.field_1635, 3
	);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(
		0, VertexFormatElement.Format.field_1621, VertexFormatElement.Type.field_1629, 1
	);
	public static final VertexFormat POSITION_COLOR_UV_LMAP = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(UV_ELEMENT).add(LMAP_ELEMENT);
	public static final VertexFormat POSITION_COLOR_UV_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(UV_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_UV_NORMAL_2 = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_UV_COLOR_LMAP = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT).add(LMAP_ELEMENT);
	public static final VertexFormat POSITION = new VertexFormat().add(POSITION_ELEMENT);
	public static final VertexFormat POSITION_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_UV = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT);
	public static final VertexFormat POSITION_NORMAL = new VertexFormat().add(POSITION_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_UV_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_UV_NORMAL = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_UV_LMAP_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(LMAP_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_UV_COLOR_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(UV_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
}

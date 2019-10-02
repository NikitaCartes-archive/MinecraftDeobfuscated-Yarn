package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement UV_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement LMAP_ELEMENT = new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement field_20886 = new VertexFormatElement(2, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1);
	public static final VertexFormat POSITION_COLOR_UV_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(UV_ELEMENT)
		.add(field_20886)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_UV_NORMAL_2 = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(UV_ELEMENT)
		.add(LMAP_ELEMENT)
		.add(field_20886)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR_LMAP = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT).add(field_20886);
	public static final VertexFormat POSITION = new VertexFormat().add(POSITION_ELEMENT);
	public static final VertexFormat POSITION_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_UV = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT);
	public static final VertexFormat field_20887 = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(UV_ELEMENT);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat field_20888 = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(UV_ELEMENT).add(field_20886);
	@Deprecated
	public static final VertexFormat POSITION_UV_LMAP_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(UV_ELEMENT).add(field_20886).add(COLOR_ELEMENT);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(UV_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
}

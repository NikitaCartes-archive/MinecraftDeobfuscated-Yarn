package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3);
	public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4);
	public static final VertexFormatElement TEXTURE_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement OVERLAY_ELEMENT = new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
	public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3);
	public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1);
	public static final VertexFormat POSITION_COLOR_UV_LMAP = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(OVERLAY_ELEMENT);
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(OVERLAY_ELEMENT);
	public static final VertexFormat POSITION = new VertexFormat().add(POSITION_ELEMENT);
	public static final VertexFormat POSITION_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE = new VertexFormat().add(POSITION_ELEMENT).add(TEXTURE_ELEMENT);
	public static final VertexFormat POSITION_NORMAL = new VertexFormat().add(POSITION_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat().add(POSITION_ELEMENT).add(TEXTURE_ELEMENT).add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_UV_NORMAL = new VertexFormat().add(POSITION_ELEMENT).add(TEXTURE_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(OVERLAY_ELEMENT)
		.add(COLOR_ELEMENT);
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat()
		.add(POSITION_ELEMENT)
		.add(TEXTURE_ELEMENT)
		.add(COLOR_ELEMENT)
		.add(NORMAL_ELEMENT)
		.add(PADDING_ELEMENT);
}

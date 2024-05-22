package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Holding vertex formats and vertex format elements.
 */
@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormat BLIT_SCREEN = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV0", VertexFormatElement.UV_0)
		.add("UV2", VertexFormatElement.UV_2)
		.add("Normal", VertexFormatElement.NORMAL)
		.skip(1)
		.build();
	public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV0", VertexFormatElement.UV_0)
		.add("UV1", VertexFormatElement.UV_1)
		.add("UV2", VertexFormatElement.UV_2)
		.add("Normal", VertexFormatElement.NORMAL)
		.skip(1)
		.build();
	public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV_0)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV2", VertexFormatElement.UV_2)
		.build();
	public static final VertexFormat POSITION = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
	public static final VertexFormat POSITION_COLOR = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.build();
	public static final VertexFormat LINES = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("Normal", VertexFormatElement.NORMAL)
		.skip(1)
		.build();
	public static final VertexFormat POSITION_COLOR_LIGHT = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV2", VertexFormatElement.UV_2)
		.build();
	public static final VertexFormat POSITION_TEXTURE = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV_0)
		.build();
	public static final VertexFormat POSITION_TEXTURE_COLOR = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV_0)
		.add("Color", VertexFormatElement.COLOR)
		.build();
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("Color", VertexFormatElement.COLOR)
		.add("UV0", VertexFormatElement.UV_0)
		.add("UV2", VertexFormatElement.UV_2)
		.build();
	public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV_0)
		.add("UV2", VertexFormatElement.UV_2)
		.add("Color", VertexFormatElement.COLOR)
		.build();
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV_0)
		.add("Color", VertexFormatElement.COLOR)
		.add("Normal", VertexFormatElement.NORMAL)
		.skip(1)
		.build();
}

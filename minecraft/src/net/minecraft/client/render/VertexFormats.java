package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Holding vertex formats and vertex format elements.
 */
@Environment(EnvType.CLIENT)
public class VertexFormats {
	public static final VertexFormat BLIT_SCREEN = VertexFormat.method_60833().method_60842("Position", VertexFormatElement.field_52107).method_60840();
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60842("Normal", VertexFormatElement.field_52113)
		.method_60841(1)
		.method_60840();
	public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("UV1", VertexFormatElement.field_52111)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60842("Normal", VertexFormatElement.field_52113)
		.method_60841(1)
		.method_60840();
	public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60840();
	public static final VertexFormat POSITION = VertexFormat.method_60833().method_60842("Position", VertexFormatElement.field_52107).method_60840();
	public static final VertexFormat POSITION_COLOR = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60840();
	public static final VertexFormat LINES = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("Normal", VertexFormatElement.field_52113)
		.method_60841(1)
		.method_60840();
	public static final VertexFormat POSITION_COLOR_LIGHT = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60840();
	public static final VertexFormat POSITION_TEXTURE = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60840();
	public static final VertexFormat POSITION_TEXTURE_COLOR = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60840();
	public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60840();
	public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("UV2", VertexFormatElement.field_52112)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60840();
	public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = VertexFormat.method_60833()
		.method_60842("Position", VertexFormatElement.field_52107)
		.method_60842("UV0", VertexFormatElement.field_52109)
		.method_60842("Color", VertexFormatElement.field_52108)
		.method_60842("Normal", VertexFormatElement.field_52113)
		.method_60841(1)
		.method_60840();
}

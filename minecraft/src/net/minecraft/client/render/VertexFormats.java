package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
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
	public static final VertexFormat POSITION_COLOR_UV_NORMAL = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder()
			.add(POSITION_ELEMENT)
			.add(COLOR_ELEMENT)
			.add(UV_ELEMENT)
			.add(field_20886)
			.add(NORMAL_ELEMENT)
			.add(PADDING_ELEMENT)
			.build()
	);
	public static final VertexFormat POSITION_UV_NORMAL_2 = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder()
			.add(POSITION_ELEMENT)
			.add(COLOR_ELEMENT)
			.add(UV_ELEMENT)
			.add(LMAP_ELEMENT)
			.add(field_20886)
			.add(NORMAL_ELEMENT)
			.add(PADDING_ELEMENT)
			.build()
	);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR_LMAP = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT).add(field_20886).build()
	);
	public static final VertexFormat POSITION = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).build());
	public static final VertexFormat POSITION_COLOR = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(COLOR_ELEMENT).build()
	);
	public static final VertexFormat field_21468 = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(field_20886).build()
	);
	public static final VertexFormat POSITION_UV = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(UV_ELEMENT).build());
	public static final VertexFormat POSITION_COLOR_UV = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(UV_ELEMENT).build()
	);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT).build()
	);
	public static final VertexFormat field_20888 = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(COLOR_ELEMENT).add(UV_ELEMENT).add(field_20886).build()
	);
	@Deprecated
	public static final VertexFormat POSITION_UV_LMAP_COLOR = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(UV_ELEMENT).add(field_20886).add(COLOR_ELEMENT).build()
	);
	@Deprecated
	public static final VertexFormat POSITION_UV_COLOR_NORMAL = new VertexFormat(
		ImmutableList.<VertexFormatElement>builder().add(POSITION_ELEMENT).add(UV_ELEMENT).add(COLOR_ELEMENT).add(NORMAL_ELEMENT).add(PADDING_ELEMENT).build()
	);
}

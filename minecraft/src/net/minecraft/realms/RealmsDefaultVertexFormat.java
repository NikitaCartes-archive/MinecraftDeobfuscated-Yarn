package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(EnvType.CLIENT)
public class RealmsDefaultVertexFormat {
	public static final RealmsVertexFormat BLOCK = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat BLOCK_NORMALS = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat ENTITY = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat PARTICLE = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_COLOR = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_TEX = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_NORMAL = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_TEX_COLOR = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_TEX_NORMAL = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_TEX2_COLOR = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormat POSITION_TEX_COLOR_NORMAL = new RealmsVertexFormat(new VertexFormat());
	public static final RealmsVertexFormatElement ELEMENT_POSITION = new RealmsVertexFormatElement(
		new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3)
	);
	public static final RealmsVertexFormatElement ELEMENT_COLOR = new RealmsVertexFormatElement(
		new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4)
	);
	public static final RealmsVertexFormatElement ELEMENT_UV0 = new RealmsVertexFormatElement(
		new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2)
	);
	public static final RealmsVertexFormatElement ELEMENT_UV1 = new RealmsVertexFormatElement(
		new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2)
	);
	public static final RealmsVertexFormatElement ELEMENT_NORMAL = new RealmsVertexFormatElement(
		new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3)
	);
	public static final RealmsVertexFormatElement ELEMENT_PADDING = new RealmsVertexFormatElement(
		new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1)
	);

	static {
		BLOCK.addElement(ELEMENT_POSITION);
		BLOCK.addElement(ELEMENT_COLOR);
		BLOCK.addElement(ELEMENT_UV0);
		BLOCK.addElement(ELEMENT_UV1);
		BLOCK_NORMALS.addElement(ELEMENT_POSITION);
		BLOCK_NORMALS.addElement(ELEMENT_COLOR);
		BLOCK_NORMALS.addElement(ELEMENT_UV0);
		BLOCK_NORMALS.addElement(ELEMENT_NORMAL);
		BLOCK_NORMALS.addElement(ELEMENT_PADDING);
		ENTITY.addElement(ELEMENT_POSITION);
		ENTITY.addElement(ELEMENT_UV0);
		ENTITY.addElement(ELEMENT_NORMAL);
		ENTITY.addElement(ELEMENT_PADDING);
		PARTICLE.addElement(ELEMENT_POSITION);
		PARTICLE.addElement(ELEMENT_UV0);
		PARTICLE.addElement(ELEMENT_COLOR);
		PARTICLE.addElement(ELEMENT_UV1);
		POSITION.addElement(ELEMENT_POSITION);
		POSITION_COLOR.addElement(ELEMENT_POSITION);
		POSITION_COLOR.addElement(ELEMENT_COLOR);
		POSITION_TEX.addElement(ELEMENT_POSITION);
		POSITION_TEX.addElement(ELEMENT_UV0);
		POSITION_NORMAL.addElement(ELEMENT_POSITION);
		POSITION_NORMAL.addElement(ELEMENT_NORMAL);
		POSITION_NORMAL.addElement(ELEMENT_PADDING);
		POSITION_TEX_COLOR.addElement(ELEMENT_POSITION);
		POSITION_TEX_COLOR.addElement(ELEMENT_UV0);
		POSITION_TEX_COLOR.addElement(ELEMENT_COLOR);
		POSITION_TEX_NORMAL.addElement(ELEMENT_POSITION);
		POSITION_TEX_NORMAL.addElement(ELEMENT_UV0);
		POSITION_TEX_NORMAL.addElement(ELEMENT_NORMAL);
		POSITION_TEX_NORMAL.addElement(ELEMENT_PADDING);
		POSITION_TEX2_COLOR.addElement(ELEMENT_POSITION);
		POSITION_TEX2_COLOR.addElement(ELEMENT_UV0);
		POSITION_TEX2_COLOR.addElement(ELEMENT_UV1);
		POSITION_TEX2_COLOR.addElement(ELEMENT_COLOR);
		POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_POSITION);
		POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_UV0);
		POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_COLOR);
		POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_NORMAL);
		POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_PADDING);
	}
}

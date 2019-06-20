package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_293;
import net.minecraft.class_296;

@Environment(EnvType.CLIENT)
public class RealmsDefaultVertexFormat {
	public static final RealmsVertexFormat BLOCK = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat BLOCK_NORMALS = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat ENTITY = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat PARTICLE = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_COLOR = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_TEX = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_NORMAL = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_TEX_COLOR = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_TEX_NORMAL = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_TEX2_COLOR = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormat POSITION_TEX_COLOR_NORMAL = new RealmsVertexFormat(new class_293());
	public static final RealmsVertexFormatElement ELEMENT_POSITION = new RealmsVertexFormatElement(
		new class_296(0, class_296.class_297.field_1623, class_296.class_298.field_1633, 3)
	);
	public static final RealmsVertexFormatElement ELEMENT_COLOR = new RealmsVertexFormatElement(
		new class_296(0, class_296.class_297.field_1624, class_296.class_298.field_1632, 4)
	);
	public static final RealmsVertexFormatElement ELEMENT_UV0 = new RealmsVertexFormatElement(
		new class_296(0, class_296.class_297.field_1623, class_296.class_298.field_1636, 2)
	);
	public static final RealmsVertexFormatElement ELEMENT_UV1 = new RealmsVertexFormatElement(
		new class_296(1, class_296.class_297.field_1625, class_296.class_298.field_1636, 2)
	);
	public static final RealmsVertexFormatElement ELEMENT_NORMAL = new RealmsVertexFormatElement(
		new class_296(0, class_296.class_297.field_1621, class_296.class_298.field_1635, 3)
	);
	public static final RealmsVertexFormatElement ELEMENT_PADDING = new RealmsVertexFormatElement(
		new class_296(0, class_296.class_297.field_1621, class_296.class_298.field_1629, 1)
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

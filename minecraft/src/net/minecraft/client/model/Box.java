package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;

@Environment(EnvType.CLIENT)
public class Box {
	private final Vertex[] vertices;
	private final Quad[] polygons;
	public final float xMin;
	public final float yMin;
	public final float zMin;
	public final float xMax;
	public final float yMax;
	public final float zMax;
	public String name;

	public Box(ModelPart parent, int textureOffsetU, int textureOffsetV, float xMin, float yMin, float zMin, int xSize, int ySize, int zSize, float scale) {
		this(parent, textureOffsetU, textureOffsetV, xMin, yMin, zMin, xSize, ySize, zSize, scale, parent.mirror);
	}

	public Box(
		ModelPart parent, int textureOffsetU, int textureOffsetV, float xMin, float yMin, float zMin, int xSize, int ySize, int zSize, float scale, boolean flip
	) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
		this.xMax = xMin + (float)xSize;
		this.yMax = yMin + (float)ySize;
		this.zMax = zMin + (float)zSize;
		this.vertices = new Vertex[8];
		this.polygons = new Quad[6];
		float f = xMin + (float)xSize;
		float g = yMin + (float)ySize;
		float h = zMin + (float)zSize;
		xMin -= scale;
		yMin -= scale;
		zMin -= scale;
		f += scale;
		g += scale;
		h += scale;
		if (flip) {
			float i = f;
			f = xMin;
			xMin = i;
		}

		Vertex vertex = new Vertex(xMin, yMin, zMin, 0.0F, 0.0F);
		Vertex vertex2 = new Vertex(f, yMin, zMin, 0.0F, 8.0F);
		Vertex vertex3 = new Vertex(f, g, zMin, 8.0F, 8.0F);
		Vertex vertex4 = new Vertex(xMin, g, zMin, 8.0F, 0.0F);
		Vertex vertex5 = new Vertex(xMin, yMin, h, 0.0F, 0.0F);
		Vertex vertex6 = new Vertex(f, yMin, h, 0.0F, 8.0F);
		Vertex vertex7 = new Vertex(f, g, h, 8.0F, 8.0F);
		Vertex vertex8 = new Vertex(xMin, g, h, 8.0F, 0.0F);
		this.vertices[0] = vertex;
		this.vertices[1] = vertex2;
		this.vertices[2] = vertex3;
		this.vertices[3] = vertex4;
		this.vertices[4] = vertex5;
		this.vertices[5] = vertex6;
		this.vertices[6] = vertex7;
		this.vertices[7] = vertex8;
		this.polygons[0] = new Quad(
			new Vertex[]{vertex6, vertex2, vertex3, vertex7},
			textureOffsetU + zSize + xSize,
			textureOffsetV + zSize,
			textureOffsetU + zSize + xSize + zSize,
			textureOffsetV + zSize + ySize,
			parent.textureWidth,
			parent.textureHeight
		);
		this.polygons[1] = new Quad(
			new Vertex[]{vertex, vertex5, vertex8, vertex4},
			textureOffsetU,
			textureOffsetV + zSize,
			textureOffsetU + zSize,
			textureOffsetV + zSize + ySize,
			parent.textureWidth,
			parent.textureHeight
		);
		this.polygons[2] = new Quad(
			new Vertex[]{vertex6, vertex5, vertex, vertex2},
			textureOffsetU + zSize,
			textureOffsetV,
			textureOffsetU + zSize + xSize,
			textureOffsetV + zSize,
			parent.textureWidth,
			parent.textureHeight
		);
		this.polygons[3] = new Quad(
			new Vertex[]{vertex3, vertex4, vertex8, vertex7},
			textureOffsetU + zSize + xSize,
			textureOffsetV + zSize,
			textureOffsetU + zSize + xSize + xSize,
			textureOffsetV,
			parent.textureWidth,
			parent.textureHeight
		);
		this.polygons[4] = new Quad(
			new Vertex[]{vertex2, vertex, vertex4, vertex3},
			textureOffsetU + zSize,
			textureOffsetV + zSize,
			textureOffsetU + zSize + xSize,
			textureOffsetV + zSize + ySize,
			parent.textureWidth,
			parent.textureHeight
		);
		this.polygons[5] = new Quad(
			new Vertex[]{vertex5, vertex6, vertex7, vertex8},
			textureOffsetU + zSize + xSize + zSize,
			textureOffsetV + zSize,
			textureOffsetU + zSize + xSize + zSize + xSize,
			textureOffsetV + zSize + ySize,
			parent.textureWidth,
			parent.textureHeight
		);
		if (flip) {
			for (Quad quad : this.polygons) {
				quad.flip();
			}
		}
	}

	public void render(BufferBuilder bufferBuilder, float scale) {
		for (Quad quad : this.polygons) {
			quad.render(bufferBuilder, scale);
		}
	}

	public Box setName(String name) {
		this.name = name;
		return this;
	}
}

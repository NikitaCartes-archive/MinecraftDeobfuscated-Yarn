package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;

@Environment(EnvType.CLIENT)
public class Cuboid {
	private final Vertex[] vertices;
	private final Quad[] polygons;
	public final float xMin;
	public final float yMin;
	public final float zMin;
	public final float xMax;
	public final float yMax;
	public final float zMax;
	public String name;

	public Cuboid(ModelPart modelPart, int i, int j, float f, float g, float h, int k, int l, int m, float n) {
		this(modelPart, i, j, f, g, h, k, l, m, n, modelPart.mirror);
	}

	public Cuboid(ModelPart modelPart, int i, int j, float f, float g, float h, int k, int l, int m, float n, boolean bl) {
		this.xMin = f;
		this.yMin = g;
		this.zMin = h;
		this.xMax = f + (float)k;
		this.yMax = g + (float)l;
		this.zMax = h + (float)m;
		this.vertices = new Vertex[8];
		this.polygons = new Quad[6];
		float o = f + (float)k;
		float p = g + (float)l;
		float q = h + (float)m;
		f -= n;
		g -= n;
		h -= n;
		o += n;
		p += n;
		q += n;
		if (bl) {
			float r = o;
			o = f;
			f = r;
		}

		Vertex vertex = new Vertex(f, g, h, 0.0F, 0.0F);
		Vertex vertex2 = new Vertex(o, g, h, 0.0F, 8.0F);
		Vertex vertex3 = new Vertex(o, p, h, 8.0F, 8.0F);
		Vertex vertex4 = new Vertex(f, p, h, 8.0F, 0.0F);
		Vertex vertex5 = new Vertex(f, g, q, 0.0F, 0.0F);
		Vertex vertex6 = new Vertex(o, g, q, 0.0F, 8.0F);
		Vertex vertex7 = new Vertex(o, p, q, 8.0F, 8.0F);
		Vertex vertex8 = new Vertex(f, p, q, 8.0F, 0.0F);
		this.vertices[0] = vertex;
		this.vertices[1] = vertex2;
		this.vertices[2] = vertex3;
		this.vertices[3] = vertex4;
		this.vertices[4] = vertex5;
		this.vertices[5] = vertex6;
		this.vertices[6] = vertex7;
		this.vertices[7] = vertex8;
		this.polygons[0] = new Quad(
			new Vertex[]{vertex6, vertex2, vertex3, vertex7}, i + m + k, j + m, i + m + k + m, j + m + l, modelPart.textureWidth, modelPart.textureHeight
		);
		this.polygons[1] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, i, j + m, i + m, j + m + l, modelPart.textureWidth, modelPart.textureHeight);
		this.polygons[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, i + m, j, i + m + k, j + m, modelPart.textureWidth, modelPart.textureHeight);
		this.polygons[3] = new Quad(
			new Vertex[]{vertex3, vertex4, vertex8, vertex7}, i + m + k, j + m, i + m + k + k, j, modelPart.textureWidth, modelPart.textureHeight
		);
		this.polygons[4] = new Quad(
			new Vertex[]{vertex2, vertex, vertex4, vertex3}, i + m, j + m, i + m + k, j + m + l, modelPart.textureWidth, modelPart.textureHeight
		);
		this.polygons[5] = new Quad(
			new Vertex[]{vertex5, vertex6, vertex7, vertex8}, i + m + k + m, j + m, i + m + k + m + k, j + m + l, modelPart.textureWidth, modelPart.textureHeight
		);
		if (bl) {
			for (Quad quad : this.polygons) {
				quad.flip();
			}
		}
	}

	public void render(BufferBuilder bufferBuilder, float f) {
		for (Quad quad : this.polygons) {
			quad.render(bufferBuilder, f);
		}
	}

	public Cuboid setName(String string) {
		this.name = string;
		return this;
	}
}

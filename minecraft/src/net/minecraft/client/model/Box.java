package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;

@Environment(EnvType.CLIENT)
public class Box {
	private final Vertex[] vertices;
	private final Polygon[] polygons;
	public final float xMin;
	public final float yMin;
	public final float zMin;
	public final float xMax;
	public final float yMax;
	public final float zMax;
	public String name;

	public Box(Cuboid cuboid, int i, int j, float f, float g, float h, int k, int l, int m, float n) {
		this(cuboid, i, j, f, g, h, k, l, m, n, cuboid.mirror);
	}

	public Box(Cuboid cuboid, int i, int j, float f, float g, float h, int k, int l, int m, float n, boolean bl) {
		this.xMin = f;
		this.yMin = g;
		this.zMin = h;
		this.xMax = f + (float)k;
		this.yMax = g + (float)l;
		this.zMax = h + (float)m;
		this.vertices = new Vertex[8];
		this.polygons = new Polygon[6];
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
		this.polygons[0] = new Polygon(
			new Vertex[]{vertex6, vertex2, vertex3, vertex7}, i + m + k, j + m, i + m + k + m, j + m + l, cuboid.textureWidth, cuboid.textureHeight
		);
		this.polygons[1] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, i, j + m, i + m, j + m + l, cuboid.textureWidth, cuboid.textureHeight);
		this.polygons[2] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, i + m, j, i + m + k, j + m, cuboid.textureWidth, cuboid.textureHeight);
		this.polygons[3] = new Polygon(
			new Vertex[]{vertex3, vertex4, vertex8, vertex7}, i + m + k, j + m, i + m + k + k, j, cuboid.textureWidth, cuboid.textureHeight
		);
		this.polygons[4] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, i + m, j + m, i + m + k, j + m + l, cuboid.textureWidth, cuboid.textureHeight);
		this.polygons[5] = new Polygon(
			new Vertex[]{vertex5, vertex6, vertex7, vertex8}, i + m + k + m, j + m, i + m + k + m + k, j + m + l, cuboid.textureWidth, cuboid.textureHeight
		);
		if (bl) {
			for (Polygon polygon : this.polygons) {
				polygon.flip();
			}
		}
	}

	public void render(BufferBuilder bufferBuilder, float f) {
		for (Polygon polygon : this.polygons) {
			polygon.render(bufferBuilder, f);
		}
	}

	public Box setName(String string) {
		this.name = string;
		return this;
	}
}

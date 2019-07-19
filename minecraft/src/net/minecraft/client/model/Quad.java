package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class Quad {
	public Vertex[] vertices;
	public final int vertexCount;
	private boolean flipNormals;

	public Quad(Vertex[] vertexs) {
		this.vertices = vertexs;
		this.vertexCount = vertexs.length;
	}

	public Quad(Vertex[] vertices, int i, int j, int k, int l, float f, float g) {
		this(vertices);
		float h = 0.0F / f;
		float m = 0.0F / g;
		vertices[0] = vertices[0].remap((float)k / f - h, (float)j / g + m);
		vertices[1] = vertices[1].remap((float)i / f + h, (float)j / g + m);
		vertices[2] = vertices[2].remap((float)i / f + h, (float)l / g - m);
		vertices[3] = vertices[3].remap((float)k / f - h, (float)l / g - m);
	}

	public void flip() {
		Vertex[] vertexs = new Vertex[this.vertices.length];

		for (int i = 0; i < this.vertices.length; i++) {
			vertexs[i] = this.vertices[this.vertices.length - i - 1];
		}

		this.vertices = vertexs;
	}

	public void render(BufferBuilder bufferBuilder, float scale) {
		Vec3d vec3d = this.vertices[1].pos.reverseSubtract(this.vertices[0].pos);
		Vec3d vec3d2 = this.vertices[1].pos.reverseSubtract(this.vertices[2].pos);
		Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
		float f = (float)vec3d3.x;
		float g = (float)vec3d3.y;
		float h = (float)vec3d3.z;
		if (this.flipNormals) {
			f = -f;
			g = -g;
			h = -h;
		}

		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

		for (int i = 0; i < 4; i++) {
			Vertex vertex = this.vertices[i];
			bufferBuilder.vertex(vertex.pos.x * (double)scale, vertex.pos.y * (double)scale, vertex.pos.z * (double)scale)
				.texture((double)vertex.u, (double)vertex.v)
				.normal(f, g, h)
				.next();
		}

		Tessellator.getInstance().draw();
	}
}

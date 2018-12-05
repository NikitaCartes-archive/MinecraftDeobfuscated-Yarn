package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class Polygon {
	public Vertex[] vertices;
	public int vertexCount;
	private boolean field_3501;

	public Polygon(Vertex[] vertexs) {
		this.vertices = vertexs;
		this.vertexCount = vertexs.length;
	}

	public Polygon(Vertex[] vertexs, int i, int j, int k, int l, float f, float g) {
		this(vertexs);
		float h = 0.0F / f;
		float m = 0.0F / g;
		vertexs[0] = vertexs[0].remap((float)k / f - h, (float)j / g + m);
		vertexs[1] = vertexs[1].remap((float)i / f + h, (float)j / g + m);
		vertexs[2] = vertexs[2].remap((float)i / f + h, (float)l / g - m);
		vertexs[3] = vertexs[3].remap((float)k / f - h, (float)l / g - m);
	}

	public void flip() {
		Vertex[] vertexs = new Vertex[this.vertices.length];

		for (int i = 0; i < this.vertices.length; i++) {
			vertexs[i] = this.vertices[this.vertices.length - i - 1];
		}

		this.vertices = vertexs;
	}

	public void render(VertexBuffer vertexBuffer, float f) {
		Vec3d vec3d = this.vertices[1].pos.reverseSubtract(this.vertices[0].pos);
		Vec3d vec3d2 = this.vertices[1].pos.reverseSubtract(this.vertices[2].pos);
		Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
		float g = (float)vec3d3.x;
		float h = (float)vec3d3.y;
		float i = (float)vec3d3.z;
		if (this.field_3501) {
			g = -g;
			h = -h;
			i = -i;
		}

		vertexBuffer.begin(7, VertexFormats.field_1580);

		for (int j = 0; j < 4; j++) {
			Vertex vertex = this.vertices[j];
			vertexBuffer.vertex(vertex.pos.x * (double)f, vertex.pos.y * (double)f, vertex.pos.z * (double)f)
				.texture((double)vertex.u, (double)vertex.v)
				.normal(g, h, i)
				.next();
		}

		Tessellator.getInstance().draw();
	}
}

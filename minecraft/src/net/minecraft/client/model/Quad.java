package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class Quad {
	public Vertex[] field_3502;
	public final int vertexCount;
	private boolean flipNormals;

	public Quad(Vertex[] vertexs) {
		this.field_3502 = vertexs;
		this.vertexCount = vertexs.length;
	}

	public Quad(Vertex[] vertexs, int i, int j, int k, int l, float f, float g) {
		this(vertexs);
		float h = 0.0F / f;
		float m = 0.0F / g;
		vertexs[0] = vertexs[0].remap((float)k / f - h, (float)j / g + m);
		vertexs[1] = vertexs[1].remap((float)i / f + h, (float)j / g + m);
		vertexs[2] = vertexs[2].remap((float)i / f + h, (float)l / g - m);
		vertexs[3] = vertexs[3].remap((float)k / f - h, (float)l / g - m);
	}

	public void flip() {
		Vertex[] vertexs = new Vertex[this.field_3502.length];

		for (int i = 0; i < this.field_3502.length; i++) {
			vertexs[i] = this.field_3502[this.field_3502.length - i - 1];
		}

		this.field_3502 = vertexs;
	}

	public void render(BufferBuilder bufferBuilder, float f) {
		Vec3d vec3d = this.field_3502[1].pos.reverseSubtract(this.field_3502[0].pos);
		Vec3d vec3d2 = this.field_3502[1].pos.reverseSubtract(this.field_3502[2].pos);
		Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
		float g = (float)vec3d3.x;
		float h = (float)vec3d3.y;
		float i = (float)vec3d3.z;
		if (this.flipNormals) {
			g = -g;
			h = -h;
			i = -i;
		}

		bufferBuilder.method_1328(7, VertexFormats.field_1580);

		for (int j = 0; j < 4; j++) {
			Vertex vertex = this.field_3502[j];
			bufferBuilder.vertex(vertex.pos.x * (double)f, vertex.pos.y * (double)f, vertex.pos.z * (double)f)
				.texture((double)vertex.u, (double)vertex.v)
				.normal(g, h, i)
				.next();
		}

		Tessellator.getInstance().draw();
	}
}

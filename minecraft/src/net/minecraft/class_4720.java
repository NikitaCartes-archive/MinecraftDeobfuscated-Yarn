package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(EnvType.CLIENT)
public class class_4720 {
	public static VertexConsumer method_24037(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
		return new class_4720.class_4589(vertexConsumer, vertexConsumer2);
	}

	@Environment(EnvType.CLIENT)
	static class class_4589 implements VertexConsumer {
		private final VertexConsumer field_21685;
		private final VertexConsumer field_21686;

		public class_4589(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
			if (vertexConsumer == vertexConsumer2) {
				throw new IllegalArgumentException("Duplicate delegates");
			} else {
				this.field_21685 = vertexConsumer;
				this.field_21686 = vertexConsumer2;
			}
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			this.field_21685.vertex(x, y, z);
			this.field_21686.vertex(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			this.field_21685.color(red, green, blue, alpha);
			this.field_21686.color(red, green, blue, alpha);
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.field_21685.texture(u, v);
			this.field_21686.texture(u, v);
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			this.field_21685.overlay(u, v);
			this.field_21686.overlay(u, v);
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			this.field_21685.light(u, v);
			this.field_21686.light(u, v);
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			this.field_21685.normal(x, y, z);
			this.field_21686.normal(x, y, z);
			return this;
		}

		@Override
		public void method_23919(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
			this.field_21685.method_23919(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
			this.field_21686.method_23919(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
		}

		@Override
		public void next() {
			this.field_21685.next();
			this.field_21686.next();
		}
	}
}

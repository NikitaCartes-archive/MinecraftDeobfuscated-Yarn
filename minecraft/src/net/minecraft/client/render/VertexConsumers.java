package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexConsumers {
	public static VertexConsumer dual(VertexConsumer first, VertexConsumer second) {
		return new VertexConsumers.Dual(first, second);
	}

	@Environment(EnvType.CLIENT)
	static class Dual implements VertexConsumer {
		private final VertexConsumer first;
		private final VertexConsumer second;

		public Dual(VertexConsumer first, VertexConsumer second) {
			if (first == second) {
				throw new IllegalArgumentException("Duplicate delegates");
			} else {
				this.first = first;
				this.second = second;
			}
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			this.first.vertex(x, y, z);
			this.second.vertex(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			this.first.color(red, green, blue, alpha);
			this.second.color(red, green, blue, alpha);
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.first.texture(u, v);
			this.second.texture(u, v);
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			this.first.overlay(u, v);
			this.second.overlay(u, v);
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			this.first.light(u, v);
			this.second.light(u, v);
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			this.first.normal(x, y, z);
			this.second.normal(x, y, z);
			return this;
		}

		@Override
		public void elements(
			float x,
			float y,
			float z,
			float r,
			float g,
			float b,
			float a,
			float textureU,
			float textureV,
			int overlay,
			int light,
			float normalX,
			float normalY,
			float normalZ
		) {
			this.first.elements(x, y, z, r, g, b, a, textureU, textureV, overlay, light, normalX, normalY, normalZ);
			this.second.elements(x, y, z, r, g, b, a, textureU, textureV, overlay, light, normalX, normalY, normalZ);
		}

		@Override
		public void next() {
			this.first.next();
			this.second.next();
		}
	}
}

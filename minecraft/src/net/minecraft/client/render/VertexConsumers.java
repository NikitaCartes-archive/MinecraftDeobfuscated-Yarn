package net.minecraft.client.render;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexConsumers {
	public static VertexConsumer method_35668() {
		throw new IllegalArgumentException();
	}

	public static VertexConsumer method_35669(VertexConsumer vertexConsumer) {
		return vertexConsumer;
	}

	public static VertexConsumer dual(VertexConsumer first, VertexConsumer second) {
		return new VertexConsumers.Dual(first, second);
	}

	public static VertexConsumer method_35670(VertexConsumer... vertexConsumers) {
		return new VertexConsumers.class_6189(vertexConsumers);
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
		public void vertex(
			float x,
			float y,
			float z,
			float red,
			float green,
			float blue,
			float alpha,
			float u,
			float v,
			int overlay,
			int light,
			float normalX,
			float normalY,
			float normalZ
		) {
			this.first.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
			this.second.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
		}

		@Override
		public void next() {
			this.first.next();
			this.second.next();
		}

		@Override
		public void fixedColor(int i, int j, int k, int l) {
			this.first.fixedColor(i, j, k, l);
			this.second.fixedColor(i, j, k, l);
		}

		@Override
		public void method_35666() {
			this.first.method_35666();
			this.second.method_35666();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_6189 implements VertexConsumer {
		private final VertexConsumer[] field_32053;

		public class_6189(VertexConsumer[] vertexConsumers) {
			for (int i = 0; i < vertexConsumers.length; i++) {
				for (int j = i + 1; j < vertexConsumers.length; j++) {
					if (vertexConsumers[i] == vertexConsumers[j]) {
						throw new IllegalArgumentException("Duplicate delegates");
					}
				}
			}

			this.field_32053 = vertexConsumers;
		}

		private void method_35677(Consumer<VertexConsumer> consumer) {
			for (VertexConsumer vertexConsumer : this.field_32053) {
				consumer.accept(vertexConsumer);
			}
		}

		@Override
		public VertexConsumer vertex(double x, double y, double z) {
			this.method_35677(vertexConsumer -> vertexConsumer.vertex(x, y, z));
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			this.method_35677(vertexConsumer -> vertexConsumer.color(red, green, blue, alpha));
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.method_35677(vertexConsumer -> vertexConsumer.texture(u, v));
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			this.method_35677(vertexConsumer -> vertexConsumer.overlay(u, v));
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			this.method_35677(vertexConsumer -> vertexConsumer.light(u, v));
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			this.method_35677(vertexConsumer -> vertexConsumer.normal(x, y, z));
			return this;
		}

		@Override
		public void vertex(
			float x,
			float y,
			float z,
			float red,
			float green,
			float blue,
			float alpha,
			float u,
			float v,
			int overlay,
			int light,
			float normalX,
			float normalY,
			float normalZ
		) {
			this.method_35677(vertexConsumer -> vertexConsumer.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ));
		}

		@Override
		public void next() {
			this.method_35677(VertexConsumer::next);
		}

		@Override
		public void fixedColor(int i, int j, int k, int l) {
			this.method_35677(vertexConsumer -> vertexConsumer.fixedColor(i, j, k, l));
		}

		@Override
		public void method_35666() {
			this.method_35677(VertexConsumer::method_35666);
		}
	}
}

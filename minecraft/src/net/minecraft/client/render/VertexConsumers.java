package net.minecraft.client.render;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A utility for combining multiple VertexConsumers into one.
 */
@Environment(EnvType.CLIENT)
public class VertexConsumers {
	/**
	 * Generates a union of zero VertexConsumers.
	 * <p>
	 * Obviously this is not possible.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static VertexConsumer union() {
		throw new IllegalArgumentException();
	}

	public static VertexConsumer union(VertexConsumer first) {
		return first;
	}

	public static VertexConsumer union(VertexConsumer first, VertexConsumer second) {
		return new VertexConsumers.Dual(first, second);
	}

	public static VertexConsumer union(VertexConsumer... delegates) {
		return new VertexConsumers.Union(delegates);
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
		public VertexConsumer vertex(float f, float g, float h) {
			this.first.vertex(f, g, h);
			this.second.vertex(f, g, h);
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
		public VertexConsumer method_60796(int i, int j) {
			this.first.method_60796(i, j);
			this.second.method_60796(i, j);
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
		public void vertex(float x, float y, float z, int i, float green, float blue, int j, int k, float v, float f, float g) {
			this.first.vertex(x, y, z, i, green, blue, j, k, v, f, g);
			this.second.vertex(x, y, z, i, green, blue, j, k, v, f, g);
		}
	}

	@Environment(EnvType.CLIENT)
	static record Union(VertexConsumer[] delegates) implements VertexConsumer {
		Union(VertexConsumer[] delegates) {
			for (int i = 0; i < delegates.length; i++) {
				for (int j = i + 1; j < delegates.length; j++) {
					if (delegates[i] == delegates[j]) {
						throw new IllegalArgumentException("Duplicate delegates");
					}
				}
			}

			this.delegates = delegates;
		}

		private void delegate(Consumer<VertexConsumer> action) {
			for (VertexConsumer vertexConsumer : this.delegates) {
				action.accept(vertexConsumer);
			}
		}

		@Override
		public VertexConsumer vertex(float f, float g, float h) {
			this.delegate(vertexConsumer -> vertexConsumer.vertex(f, g, h));
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			this.delegate(vertexConsumer -> vertexConsumer.color(red, green, blue, alpha));
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			this.delegate(vertexConsumer -> vertexConsumer.texture(u, v));
			return this;
		}

		@Override
		public VertexConsumer method_60796(int i, int j) {
			this.delegate(vertexConsumer -> vertexConsumer.method_60796(i, j));
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			this.delegate(vertexConsumer -> vertexConsumer.light(u, v));
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			this.delegate(vertexConsumer -> vertexConsumer.normal(x, y, z));
			return this;
		}

		@Override
		public void vertex(float x, float y, float z, int i, float green, float blue, int j, int k, float v, float f, float g) {
			this.delegate(vertexConsumer -> vertexConsumer.vertex(x, y, z, i, green, blue, j, k, v, f, g));
		}
	}
}

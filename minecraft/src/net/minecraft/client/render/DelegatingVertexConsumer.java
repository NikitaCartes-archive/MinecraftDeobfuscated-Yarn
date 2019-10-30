package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DelegatingVertexConsumer implements VertexConsumer {
	private final Iterable<VertexConsumer> delegates;

	public DelegatingVertexConsumer(ImmutableList<VertexConsumer> delegates) {
		for (int i = 0; i < delegates.size(); i++) {
			for (int j = i + 1; j < delegates.size(); j++) {
				if (delegates.get(i) == delegates.get(j)) {
					throw new IllegalArgumentException("Duplicate delegates");
				}
			}
		}

		this.delegates = delegates;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.vertex(x, y, z));
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.color(red, green, blue, alpha));
		return this;
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.texture(u, v));
		return this;
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.overlay(u, v));
		return this;
	}

	@Override
	public VertexConsumer light(int u, int v) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.light(u, v));
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.normal(x, y, z));
		return this;
	}

	@Override
	public void next() {
		this.delegates.forEach(VertexConsumer::next);
	}
}

package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DelegatingVertexConsumer implements VertexConsumer {
	private final Iterable<VertexConsumer> delegates;

	public DelegatingVertexConsumer(ImmutableList<VertexConsumer> immutableList) {
		for (int i = 0; i < immutableList.size(); i++) {
			for (int j = i + 1; j < immutableList.size(); j++) {
				if (immutableList.get(i) == immutableList.get(j)) {
					throw new IllegalArgumentException("Duplicate delegates");
				}
			}
		}

		this.delegates = immutableList;
	}

	@Override
	public VertexConsumer vertex(double d, double e, double f) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.vertex(d, e, f));
		return this;
	}

	@Override
	public VertexConsumer color(int i, int j, int k, int l) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.color(i, j, k, l));
		return this;
	}

	@Override
	public VertexConsumer texture(float f, float g) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.texture(f, g));
		return this;
	}

	@Override
	public VertexConsumer overlay(int i, int j) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.light(i, j));
		return this;
	}

	@Override
	public VertexConsumer light(int i, int j) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.light(i, j));
		return this;
	}

	@Override
	public VertexConsumer normal(float f, float g, float h) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.normal(f, g, h));
		return this;
	}

	@Override
	public void next() {
		this.delegates.forEach(VertexConsumer::next);
	}

	@Override
	public void defaultOverlay(int i, int j) {
		this.delegates.forEach(vertexConsumer -> vertexConsumer.defaultOverlay(i, j));
	}

	@Override
	public void clearDefaultOverlay() {
		this.delegates.forEach(VertexConsumer::clearDefaultOverlay);
	}
}

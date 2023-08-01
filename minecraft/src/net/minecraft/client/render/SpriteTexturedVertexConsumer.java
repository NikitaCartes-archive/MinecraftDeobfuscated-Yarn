package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public class SpriteTexturedVertexConsumer implements VertexConsumer {
	private final VertexConsumer delegate;
	private final Sprite sprite;

	public SpriteTexturedVertexConsumer(VertexConsumer delegate, Sprite sprite) {
		this.delegate = delegate;
		this.sprite = sprite;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return this.delegate.vertex(x, y, z);
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return this.delegate.color(red, green, blue, alpha);
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		return this.delegate.texture(this.sprite.getFrameU(u), this.sprite.getFrameV(v));
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return this.delegate.overlay(u, v);
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return this.delegate.light(u, v);
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return this.delegate.normal(x, y, z);
	}

	@Override
	public void next() {
		this.delegate.next();
	}

	@Override
	public void fixedColor(int red, int green, int blue, int alpha) {
		this.delegate.fixedColor(red, green, blue, alpha);
	}

	@Override
	public void unfixColor() {
		this.delegate.unfixColor();
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
		this.delegate.vertex(x, y, z, red, green, blue, alpha, this.sprite.getFrameU(u), this.sprite.getFrameV(v), overlay, light, normalX, normalY, normalZ);
	}
}

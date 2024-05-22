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
	public VertexConsumer vertex(float f, float g, float h) {
		return this.delegate.vertex(f, g, h);
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
	public VertexConsumer method_60796(int i, int j) {
		return this.delegate.method_60796(i, j);
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
	public void vertex(float x, float y, float z, int i, float green, float blue, int j, int k, float v, float f, float g) {
		this.delegate.vertex(x, y, z, i, this.sprite.getFrameU(green), this.sprite.getFrameV(blue), j, k, v, f, g);
	}
}

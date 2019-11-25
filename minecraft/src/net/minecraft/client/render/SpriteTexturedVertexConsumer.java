package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public class SpriteTexturedVertexConsumer implements VertexConsumer {
	private final VertexConsumer parent;
	private final Sprite sprite;

	public SpriteTexturedVertexConsumer(VertexConsumer parent, Sprite sprite) {
		this.parent = parent;
		this.sprite = sprite;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return this.parent.vertex(x, y, z);
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return this.parent.color(red, green, blue, alpha);
	}

	@Override
	public VertexConsumer texture(float u, float v) {
		return this.parent.texture(this.sprite.getFrameU((double)(u * 16.0F)), this.sprite.getFrameV((double)(v * 16.0F)));
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return this.parent.overlay(u, v);
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return this.parent.light(u, v);
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return this.parent.normal(x, y, z);
	}

	@Override
	public void next() {
		this.parent.next();
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
		this.parent
			.elements(
				x,
				y,
				z,
				r,
				g,
				b,
				a,
				this.sprite.getFrameU((double)(textureU * 16.0F)),
				this.sprite.getFrameV((double)(textureV * 16.0F)),
				overlay,
				light,
				normalX,
				normalY,
				normalZ
			);
	}
}

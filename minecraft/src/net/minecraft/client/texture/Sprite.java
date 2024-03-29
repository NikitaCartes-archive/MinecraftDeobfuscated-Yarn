package net.minecraft.client.texture;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Sprite {
	private final Identifier atlasId;
	private final SpriteContents contents;
	final int x;
	final int y;
	private final float minU;
	private final float maxU;
	private final float minV;
	private final float maxV;

	protected Sprite(Identifier atlasId, SpriteContents contents, int atlasWidth, int atlasHeight, int x, int y) {
		this.atlasId = atlasId;
		this.contents = contents;
		this.x = x;
		this.y = y;
		this.minU = (float)x / (float)atlasWidth;
		this.maxU = (float)(x + contents.getWidth()) / (float)atlasWidth;
		this.minV = (float)y / (float)atlasHeight;
		this.maxV = (float)(y + contents.getHeight()) / (float)atlasHeight;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public float getMinU() {
		return this.minU;
	}

	public float getMaxU() {
		return this.maxU;
	}

	public SpriteContents getContents() {
		return this.contents;
	}

	@Nullable
	public Sprite.TickableAnimation createAnimation() {
		final Animator animator = this.contents.createAnimator();
		return animator != null ? new Sprite.TickableAnimation() {
			@Override
			public void tick() {
				animator.tick(Sprite.this.x, Sprite.this.y);
			}

			@Override
			public void close() {
				animator.close();
			}
		} : null;
	}

	public float getFrameU(float frame) {
		float f = this.maxU - this.minU;
		return this.minU + f * frame;
	}

	public float getFrameFromU(float u) {
		float f = this.maxU - this.minU;
		return (u - this.minU) / f;
	}

	public float getMinV() {
		return this.minV;
	}

	public float getMaxV() {
		return this.maxV;
	}

	public float getFrameV(float frame) {
		float f = this.maxV - this.minV;
		return this.minV + f * frame;
	}

	public float getFrameFromV(float v) {
		float f = this.maxV - this.minV;
		return (v - this.minV) / f;
	}

	public Identifier getAtlasId() {
		return this.atlasId;
	}

	public String toString() {
		return "TextureAtlasSprite{contents='" + this.contents + "', u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + "}";
	}

	public void upload() {
		this.contents.upload(this.x, this.y);
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.contents.getWidth() / (this.maxU - this.minU);
		float g = (float)this.contents.getHeight() / (this.maxV - this.minV);
		return Math.max(g, f);
	}

	public float getAnimationFrameDelta() {
		return 4.0F / this.getFrameDeltaFactor();
	}

	public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer consumer) {
		return new SpriteTexturedVertexConsumer(consumer, this);
	}

	@Environment(EnvType.CLIENT)
	public interface TickableAnimation extends AutoCloseable {
		void tick();

		void close();
	}
}

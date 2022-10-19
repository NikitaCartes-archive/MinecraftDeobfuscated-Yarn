package net.minecraft.client.texture;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Sprite {
	private final Identifier id;
	private final SpriteContents contents;
	final int x;
	final int y;
	private final float uMin;
	private final float uMax;
	private final float vMin;
	private final float vMax;

	protected Sprite(Identifier id, SpriteContents contents, int maxLevel, int atlasWidth, int atlasHeight, int x) {
		this.id = id;
		this.contents = contents;
		this.x = atlasHeight;
		this.y = x;
		this.uMin = (float)atlasHeight / (float)maxLevel;
		this.uMax = (float)(atlasHeight + contents.getWidth()) / (float)maxLevel;
		this.vMin = (float)x / (float)atlasWidth;
		this.vMax = (float)(x + contents.getHeight()) / (float)atlasWidth;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public float getMinU() {
		return this.uMin;
	}

	public float getMaxU() {
		return this.uMax;
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

	public float getFrameU(double frame) {
		float f = this.uMax - this.uMin;
		return this.uMin + f * (float)frame / 16.0F;
	}

	public float method_35804(float f) {
		float g = this.uMax - this.uMin;
		return (f - this.uMin) / g * 16.0F;
	}

	public float getMinV() {
		return this.vMin;
	}

	public float getMaxV() {
		return this.vMax;
	}

	public float getFrameV(double frame) {
		float f = this.vMax - this.vMin;
		return this.vMin + f * (float)frame / 16.0F;
	}

	public float method_35805(float f) {
		float g = this.vMax - this.vMin;
		return (f - this.vMin) / g * 16.0F;
	}

	public Identifier getId() {
		return this.id;
	}

	public String toString() {
		return "TextureAtlasSprite{contents='" + this.contents + "', u0=" + this.uMin + ", u1=" + this.uMax + ", v0=" + this.vMin + ", v1=" + this.vMax + "}";
	}

	public void upload() {
		this.contents.upload(this.x, this.y);
	}

	private float getFrameDeltaFactor() {
		float f = (float)this.contents.getWidth() / (this.uMax - this.uMin);
		float g = (float)this.contents.getHeight() / (this.vMax - this.vMin);
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

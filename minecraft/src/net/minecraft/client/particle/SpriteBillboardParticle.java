package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class SpriteBillboardParticle extends BillboardParticle {
	protected Sprite sprite;

	protected SpriteBillboardParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected SpriteBillboardParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	protected void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	protected float getMinU() {
		return this.sprite.getMinU();
	}

	@Override
	protected float getMaxU() {
		return this.sprite.getMaxU();
	}

	@Override
	protected float getMinV() {
		return this.sprite.getMinV();
	}

	@Override
	protected float getMaxV() {
		return this.sprite.getMaxV();
	}

	public void setSprite(SpriteProvider spriteProvider) {
		this.setSprite(spriteProvider.getSprite(this.random));
	}

	public void setSpriteForAge(SpriteProvider spriteProvider) {
		this.setSprite(spriteProvider.getSprite(this.age, this.maxAge));
	}
}

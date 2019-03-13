package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4002;
import net.minecraft.client.texture.Sprite;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class SpriteBillboardParticle extends BillboardParticle {
	protected Sprite field_17886;

	protected SpriteBillboardParticle(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected SpriteBillboardParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	protected void method_18141(Sprite sprite) {
		this.field_17886 = sprite;
	}

	@Override
	protected float getMinU() {
		return this.field_17886.getMinU();
	}

	@Override
	protected float getMaxU() {
		return this.field_17886.getMaxU();
	}

	@Override
	protected float getMinV() {
		return this.field_17886.getMinV();
	}

	@Override
	protected float getMaxV() {
		return this.field_17886.getMaxV();
	}

	public void method_18140(class_4002 arg) {
		this.method_18141(arg.method_18139(this.random));
	}

	public void method_18142(class_4002 arg) {
		this.method_18141(arg.method_18138(this.age, this.maxAge));
	}
}

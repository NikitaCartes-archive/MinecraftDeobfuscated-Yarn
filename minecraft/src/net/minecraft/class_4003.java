package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public abstract class class_4003 extends class_3940 {
	protected Sprite field_17886;

	protected class_4003(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected class_4003(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	protected void method_18141(Sprite sprite) {
		this.field_17886 = sprite;
	}

	@Override
	protected float method_18133() {
		return this.field_17886.getMinU();
	}

	@Override
	protected float method_18134() {
		return this.field_17886.getMaxU();
	}

	@Override
	protected float method_18135() {
		return this.field_17886.getMinV();
	}

	@Override
	protected float method_18136() {
		return this.field_17886.getMaxV();
	}

	public void method_18140(class_4002 arg) {
		this.method_18141(arg.method_18139(this.random));
	}

	public void method_18142(class_4002 arg) {
		this.method_18141(arg.method_18138(this.age, this.maxAge));
	}
}

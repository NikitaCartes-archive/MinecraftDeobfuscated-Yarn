package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4003;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CrackParticle extends class_4003 {
	private final float field_17783;
	private final float field_17784;

	private CrackParticle(World world, double d, double e, double f, double g, double h, double i, ItemStack itemStack) {
		this(world, d, e, f, itemStack);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17827;
	}

	protected CrackParticle(World world, double d, double e, double f, ItemStack itemStack) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.method_18141(MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(itemStack, world, null).getSprite());
		this.gravityStrength = 1.0F;
		this.field_17867 /= 2.0F;
		this.field_17783 = this.random.nextFloat() * 3.0F;
		this.field_17784 = this.random.nextFloat() * 3.0F;
	}

	@Override
	protected float method_18133() {
		return this.field_17886.getU((double)((this.field_17783 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float method_18134() {
		return this.field_17886.getU((double)(this.field_17783 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18135() {
		return this.field_17886.getV((double)(this.field_17784 / 4.0F * 16.0F));
	}

	@Override
	protected float method_18136() {
		return this.field_17886.getV((double)((this.field_17784 + 1.0F) / 4.0F * 16.0F));
	}

	@Environment(EnvType.CLIENT)
	public static class ItemFactory implements ParticleFactory<ItemStackParticleParameters> {
		public Particle method_3007(ItemStackParticleParameters itemStackParticleParameters, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, g, h, i, itemStackParticleParameters.getItemStack());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SlimeballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3008(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8777));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SnowballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3009(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.field_8543));
		}
	}
}

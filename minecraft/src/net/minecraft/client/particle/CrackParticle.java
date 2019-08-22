package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class CrackParticle extends SpriteBillboardParticle {
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
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.TERRAIN_SHEET;
	}

	protected CrackParticle(World world, double d, double e, double f, ItemStack itemStack) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setSprite(MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(itemStack, world, null).getSprite());
		this.gravityStrength = 1.0F;
		this.scale /= 2.0F;
		this.field_17783 = this.random.nextFloat() * 3.0F;
		this.field_17784 = this.random.nextFloat() * 3.0F;
	}

	@Override
	protected float getMinU() {
		return this.sprite.getU((double)((this.field_17783 + 1.0F) / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxU() {
		return this.sprite.getU((double)(this.field_17783 / 4.0F * 16.0F));
	}

	@Override
	protected float getMinV() {
		return this.sprite.getV((double)(this.field_17784 / 4.0F * 16.0F));
	}

	@Override
	protected float getMaxV() {
		return this.sprite.getV((double)((this.field_17784 + 1.0F) / 4.0F * 16.0F));
	}

	@Environment(EnvType.CLIENT)
	public static class ItemFactory implements ParticleFactory<ItemStackParticleEffect> {
		public Particle method_3007(ItemStackParticleEffect itemStackParticleEffect, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, g, h, i, itemStackParticleEffect.getItemStack());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SlimeballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3008(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.SLIME_BALL));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SnowballFactory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3009(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new CrackParticle(world, d, e, f, new ItemStack(Items.SNOWBALL));
		}
	}
}

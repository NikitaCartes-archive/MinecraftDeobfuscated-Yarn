package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BarrierParticle extends SpriteBillboardParticle {
	private BarrierParticle(World world, double d, double e, double f, ItemProvider itemProvider) {
		super(world, d, e, f);
		this.method_18141(MinecraftClient.getInstance().method_1480().getModels().method_3307(itemProvider));
		this.gravityStrength = 0.0F;
		this.maxAge = 80;
		this.collidesWithWorld = false;
	}

	@Override
	public ParticleTextureSheet method_18122() {
		return ParticleTextureSheet.TERRAIN_SHEET;
	}

	@Override
	public float method_18132(float f) {
		return 0.5F;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle method_3010(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new BarrierParticle(world, d, e, f, Blocks.field_10499.getItem());
		}
	}
}

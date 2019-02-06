package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3999;
import net.minecraft.class_4003;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BarrierParticle extends class_4003 {
	private BarrierParticle(World world, double d, double e, double f, ItemProvider itemProvider) {
		super(world, d, e, f);
		this.method_18141(MinecraftClient.getInstance().getItemRenderer().getModels().getSprite(itemProvider));
		this.gravityStrength = 0.0F;
		this.maxAge = 80;
		this.collidesWithWorld = false;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17827;
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

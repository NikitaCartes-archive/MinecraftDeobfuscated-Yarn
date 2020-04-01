package net.minecraft.world.biome;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.registry.Registry;

public class BiomeParticleConfig {
	private final ParticleEffect type;
	private final float chance;
	private final double velocityXFactory;
	private final double velocityYFactory;
	private final double velocityZFactory;

	public BiomeParticleConfig(ParticleEffect particleEffect, float chance, double d, double e, double f) {
		this.type = particleEffect;
		this.chance = chance;
		this.velocityXFactory = d;
		this.velocityYFactory = e;
		this.velocityZFactory = f;
	}

	@Environment(EnvType.CLIENT)
	public ParticleEffect getParticleType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAddParticle(Random random) {
		return random.nextFloat() <= this.chance;
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityX() {
		return this.velocityXFactory;
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityY() {
		return this.velocityYFactory;
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityZ() {
		return this.velocityZFactory;
	}

	public static BiomeParticleConfig method_26445(Random random) {
		return new BiomeParticleConfig(
			Registry.PARTICLE_TYPE.getRandom(random).method_26703(random), random.nextFloat() * 0.2F, random.nextDouble(), random.nextDouble(), random.nextDouble()
		);
	}
}

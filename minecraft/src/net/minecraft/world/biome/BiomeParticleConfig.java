package net.minecraft.world.biome;

import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DefaultParticleType;

public class BiomeParticleConfig {
	private final DefaultParticleType type;
	private final float chance;
	private final Function<Random, Double> velocityXFactory;
	private final Function<Random, Double> velocityYFactory;
	private final Function<Random, Double> velocityZFactory;

	public BiomeParticleConfig(
		DefaultParticleType type, float chance, Function<Random, Double> xFactory, Function<Random, Double> yFactory, Function<Random, Double> zFactory
	) {
		this.type = type;
		this.chance = chance;
		this.velocityXFactory = xFactory;
		this.velocityYFactory = yFactory;
		this.velocityZFactory = zFactory;
	}

	@Environment(EnvType.CLIENT)
	public DefaultParticleType getParticleType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAddParticle(Random random) {
		return random.nextFloat() <= this.chance;
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityX(Random random) {
		return (Double)this.velocityXFactory.apply(random);
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityY(Random random) {
		return (Double)this.velocityYFactory.apply(random);
	}

	@Environment(EnvType.CLIENT)
	public double generateVelocityZ(Random random) {
		return (Double)this.velocityZFactory.apply(random);
	}
}

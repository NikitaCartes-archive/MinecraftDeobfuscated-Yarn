package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BiomeParticleConfig {
	public static final Codec<BiomeParticleConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ParticleTypes.TYPE_CODEC.fieldOf("options").forGetter(biomeParticleConfig -> biomeParticleConfig.particle),
					Codec.FLOAT.fieldOf("probability").forGetter(biomeParticleConfig -> biomeParticleConfig.probability)
				)
				.apply(instance, BiomeParticleConfig::new)
	);
	private final ParticleEffect particle;
	private final float probability;

	public BiomeParticleConfig(ParticleEffect particle, float probability) {
		this.particle = particle;
		this.probability = probability;
	}

	public ParticleEffect getParticle() {
		return this.particle;
	}

	public boolean shouldAddParticle(Random random) {
		return random.nextFloat() <= this.probability;
	}
}

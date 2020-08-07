package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class BiomeParticleConfig {
	public static final Codec<BiomeParticleConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ParticleTypes.field_25125.fieldOf("options").forGetter(biomeParticleConfig -> biomeParticleConfig.particle),
					Codec.FLOAT.fieldOf("probability").forGetter(biomeParticleConfig -> biomeParticleConfig.chance)
				)
				.apply(instance, BiomeParticleConfig::new)
	);
	private final ParticleEffect particle;
	private final float chance;

	public BiomeParticleConfig(ParticleEffect particle, float f) {
		this.particle = particle;
		this.chance = f;
	}

	@Environment(EnvType.CLIENT)
	public ParticleEffect getParticle() {
		return this.particle;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldAddParticle(Random random) {
		return random.nextFloat() <= this.chance;
	}
}

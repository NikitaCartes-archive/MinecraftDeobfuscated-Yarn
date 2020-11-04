package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeCrackConfig {
	public static final Codec<GeodeCrackConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GeodeFeatureConfig.RANGE.fieldOf("generate_crack_chance").orElse(1.0).forGetter(geodeCrackConfig -> geodeCrackConfig.generateCrackChance),
					Codec.doubleRange(0.0, 5.0).fieldOf("base_crack_size").orElse(2.0).forGetter(geodeCrackConfig -> geodeCrackConfig.baseCrackSize),
					Codec.intRange(0, 10).fieldOf("crack_point_offset").orElse(2).forGetter(geodeCrackConfig -> geodeCrackConfig.crackPointOffset)
				)
				.apply(instance, GeodeCrackConfig::new)
	);
	public final double generateCrackChance;
	public final double baseCrackSize;
	public final int crackPointOffset;

	public GeodeCrackConfig(double generateCrackChance, double baseCrackSize, int crackPointOffset) {
		this.generateCrackChance = generateCrackChance;
		this.baseCrackSize = baseCrackSize;
		this.crackPointOffset = crackPointOffset;
	}
}

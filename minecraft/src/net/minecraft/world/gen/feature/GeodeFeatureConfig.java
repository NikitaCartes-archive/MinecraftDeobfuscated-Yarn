package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeFeatureConfig implements FeatureConfig {
	public static final Codec<Double> RANGE = Codec.doubleRange(0.0, 1.0);
	public static final Codec<GeodeFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					GeodeLayerConfig.CODEC.fieldOf("blocks").forGetter(geodeFeatureConfig -> geodeFeatureConfig.layerConfig),
					GeodeLayerThicknessConfig.CODEC.fieldOf("layers").forGetter(geodeFeatureConfig -> geodeFeatureConfig.layerThicknessConfig),
					GeodeCrackConfig.CODEC.fieldOf("crack").forGetter(geodeFeatureConfig -> geodeFeatureConfig.crackConfig),
					RANGE.fieldOf("use_potential_placements_chance").orElse(0.35).forGetter(geodeFeatureConfig -> geodeFeatureConfig.usePotentialPlacementsChance),
					RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0).forGetter(geodeFeatureConfig -> geodeFeatureConfig.useAlternateLayer0Chance),
					Codec.BOOL
						.fieldOf("placements_require_layer0_alternate")
						.orElse(true)
						.forGetter(geodeFeatureConfig -> geodeFeatureConfig.placementsRequireLayer0Alternate),
					Codec.intRange(1, 10).fieldOf("min_outer_wall_distance").orElse(4).forGetter(geodeFeatureConfig -> geodeFeatureConfig.minOuterWallDistance),
					Codec.intRange(1, 20).fieldOf("max_outer_wall_distance").orElse(6).forGetter(geodeFeatureConfig -> geodeFeatureConfig.maxOuterWallDistance),
					Codec.intRange(1, 10).fieldOf("min_distribution_points").orElse(3).forGetter(geodeFeatureConfig -> geodeFeatureConfig.minDistributionPoints),
					Codec.intRange(1, 20).fieldOf("max_distribution_points").orElse(5).forGetter(geodeFeatureConfig -> geodeFeatureConfig.maxDistributionPoints),
					Codec.intRange(0, 10).fieldOf("min_point_offset").orElse(1).forGetter(geodeFeatureConfig -> geodeFeatureConfig.minPointOffset),
					Codec.intRange(0, 10).fieldOf("max_point_offset").orElse(3).forGetter(geodeFeatureConfig -> geodeFeatureConfig.maxPointOffset),
					Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter(geodeFeatureConfig -> geodeFeatureConfig.minGenOffset),
					Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter(geodeFeatureConfig -> geodeFeatureConfig.maxGenOffset),
					RANGE.fieldOf("noise_multiplier").orElse(0.05).forGetter(geodeFeatureConfig -> geodeFeatureConfig.noiseMultiplier)
				)
				.apply(instance, GeodeFeatureConfig::new)
	);
	public final GeodeLayerConfig layerConfig;
	public final GeodeLayerThicknessConfig layerThicknessConfig;
	public final GeodeCrackConfig crackConfig;
	public final double usePotentialPlacementsChance;
	public final double useAlternateLayer0Chance;
	public final boolean placementsRequireLayer0Alternate;
	public final int minOuterWallDistance;
	public final int maxOuterWallDistance;
	public final int minDistributionPoints;
	public final int maxDistributionPoints;
	public final int minPointOffset;
	public final int maxPointOffset;
	public final int minGenOffset;
	public final int maxGenOffset;
	public final double noiseMultiplier;

	public GeodeFeatureConfig(
		GeodeLayerConfig layerConfig,
		GeodeLayerThicknessConfig layerThicknessConfig,
		GeodeCrackConfig crackConfig,
		double usePotentialPlacementsChance,
		double useAlternateLayer0Chance,
		boolean placementsRequireLayer0Alternate,
		int minOuterWallDistance,
		int maxOuterWallDistance,
		int minDistributionPoints,
		int maxDistributionPoints,
		int minPointOffset,
		int maxPointOffset,
		int minGenOffset,
		int maxGenOffset,
		double noiseMultiplier
	) {
		this.layerConfig = layerConfig;
		this.layerThicknessConfig = layerThicknessConfig;
		this.crackConfig = crackConfig;
		this.usePotentialPlacementsChance = usePotentialPlacementsChance;
		this.useAlternateLayer0Chance = useAlternateLayer0Chance;
		this.placementsRequireLayer0Alternate = placementsRequireLayer0Alternate;
		this.minOuterWallDistance = minOuterWallDistance;
		this.maxOuterWallDistance = maxOuterWallDistance;
		this.minDistributionPoints = minDistributionPoints;
		this.maxDistributionPoints = maxDistributionPoints;
		this.minPointOffset = minPointOffset;
		this.maxPointOffset = maxPointOffset;
		this.minGenOffset = minGenOffset;
		this.maxGenOffset = maxGenOffset;
		this.noiseMultiplier = noiseMultiplier;
	}
}

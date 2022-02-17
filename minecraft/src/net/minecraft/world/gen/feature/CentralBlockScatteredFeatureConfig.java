package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class CentralBlockScatteredFeatureConfig implements FeatureConfig {
	public static final Codec<CentralBlockScatteredFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("can_place_central_block_on").forGetter(config -> config.canPlaceCentralBlockOn),
					BlockStateProvider.TYPE_CODEC.fieldOf("central_state").forGetter(config -> config.centralState),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("scattered_feature").forGetter(config -> config.scatteredFeature),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("central_feature").forGetter(config -> config.centralFeature),
					VerticalSurfaceType.CODEC.fieldOf("surface").forGetter(config -> config.surface),
					Codec.intRange(1, 256).fieldOf("vertical_range").forGetter(config -> config.verticalRange),
					Codec.intRange(1, 256).fieldOf("feature_count_min").forGetter(config -> config.featureCountMin),
					Codec.intRange(1, 256).fieldOf("feature_count_max").forGetter(config -> config.featureCountMax),
					Codec.intRange(1, 256).fieldOf("max_feature_distance").forGetter(config -> config.maxFeatureDistance)
				)
				.apply(instance, CentralBlockScatteredFeatureConfig::new)
	);
	public final Identifier canPlaceCentralBlockOn;
	public final BlockStateProvider centralState;
	public final Supplier<ConfiguredFeature<?, ?>> scatteredFeature;
	public final Supplier<ConfiguredFeature<?, ?>> centralFeature;
	public final VerticalSurfaceType surface;
	public final int verticalRange;
	public final int featureCountMin;
	public final int featureCountMax;
	public final int maxFeatureDistance;

	public CentralBlockScatteredFeatureConfig(
		Identifier canPlaceCentralBlockOn,
		BlockStateProvider centralState,
		Supplier<ConfiguredFeature<?, ?>> scatteredFeature,
		Supplier<ConfiguredFeature<?, ?>> centralFeature,
		VerticalSurfaceType surface,
		int verticalRange,
		int featureCountMin,
		int featureCountMax,
		int maxFeatureDistance
	) {
		this.canPlaceCentralBlockOn = canPlaceCentralBlockOn;
		this.centralState = centralState;
		this.scatteredFeature = scatteredFeature;
		this.centralFeature = centralFeature;
		this.surface = surface;
		this.verticalRange = verticalRange;
		this.featureCountMin = featureCountMin;
		this.featureCountMax = featureCountMax;
		this.maxFeatureDistance = maxFeatureDistance;
	}
}

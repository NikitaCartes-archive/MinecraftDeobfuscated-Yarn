package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class SculkPatchFeatureConfig implements FeatureConfig {
	public static final Codec<SculkPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("replaceable").forGetter(config -> config.replaceable),
					BlockStateProvider.TYPE_CODEC.fieldOf("ground_state").forGetter(config -> config.groundState),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("growth_feature").forGetter(config -> config.growthFeature),
					Codec.floatRange(0.0F, 1.0F).fieldOf("growth_chance").forGetter(config -> config.growthChance),
					VerticalSurfaceType.CODEC.fieldOf("surface").forGetter(config -> config.surface),
					Codec.intRange(1, 256).fieldOf("vertical_range").forGetter(config -> config.verticalRange),
					IntProvider.VALUE_CODEC.fieldOf("xz_radius").forGetter(config -> config.xzRadius)
				)
				.apply(instance, SculkPatchFeatureConfig::new)
	);
	public final Identifier replaceable;
	public final BlockStateProvider groundState;
	public final Supplier<ConfiguredFeature<?, ?>> growthFeature;
	public final float growthChance;
	public final VerticalSurfaceType surface;
	public final int verticalRange;
	public final IntProvider xzRadius;

	public SculkPatchFeatureConfig(
		Identifier replaceable,
		BlockStateProvider groundState,
		Supplier<ConfiguredFeature<?, ?>> growthFeature,
		float growthChance,
		VerticalSurfaceType surface,
		int verticalRange,
		IntProvider xzRadius
	) {
		this.replaceable = replaceable;
		this.groundState = groundState;
		this.growthFeature = growthFeature;
		this.growthChance = growthChance;
		this.surface = surface;
		this.verticalRange = verticalRange;
		this.xzRadius = xzRadius;
	}
}

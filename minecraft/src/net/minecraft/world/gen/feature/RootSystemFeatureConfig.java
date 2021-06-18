package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class RootSystemFeatureConfig implements FeatureConfig {
	public static final Codec<RootSystemFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.feature),
					Codec.intRange(1, 64)
						.fieldOf("required_vertical_space_for_tree")
						.forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.requiredVerticalSpaceForTree),
					Codec.intRange(1, 64).fieldOf("root_radius").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.rootRadius),
					Identifier.CODEC.fieldOf("root_replaceable").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.rootReplaceable),
					BlockStateProvider.TYPE_CODEC.fieldOf("root_state_provider").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.rootStateProvider),
					Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.rootPlacementAttempts),
					Codec.intRange(1, 4096).fieldOf("root_column_max_height").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.maxRootColumnHeight),
					Codec.intRange(1, 64).fieldOf("hanging_root_radius").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.hangingRootRadius),
					Codec.intRange(0, 16).fieldOf("hanging_roots_vertical_span").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.hangingRootVerticalSpan),
					BlockStateProvider.TYPE_CODEC
						.fieldOf("hanging_root_state_provider")
						.forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.hangingRootStateProvider),
					Codec.intRange(1, 256)
						.fieldOf("hanging_root_placement_attempts")
						.forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.hangingRootPlacementAttempts),
					Codec.intRange(1, 64).fieldOf("allowed_vertical_water_for_tree").forGetter(rootSystemFeatureConfig -> rootSystemFeatureConfig.allowedVerticalWaterForTree)
				)
				.apply(instance, RootSystemFeatureConfig::new)
	);
	public final Supplier<ConfiguredFeature<?, ?>> feature;
	public final int requiredVerticalSpaceForTree;
	public final int rootRadius;
	public final Identifier rootReplaceable;
	public final BlockStateProvider rootStateProvider;
	public final int rootPlacementAttempts;
	public final int maxRootColumnHeight;
	public final int hangingRootRadius;
	public final int hangingRootVerticalSpan;
	public final BlockStateProvider hangingRootStateProvider;
	public final int hangingRootPlacementAttempts;
	public final int allowedVerticalWaterForTree;

	public RootSystemFeatureConfig(
		Supplier<ConfiguredFeature<?, ?>> feature,
		int requiredVerticalSpaceForTree,
		int rootRadius,
		Identifier rootReplaceable,
		BlockStateProvider rootStateProvider,
		int rootPlacementAttempts,
		int maxRootColumnHeight,
		int hangingRootRadius,
		int hangingRootVerticalSpan,
		BlockStateProvider hangingRootStateProvider,
		int hangingRootPlacementAttempts,
		int allowedVerticalWaterForTree
	) {
		this.feature = feature;
		this.requiredVerticalSpaceForTree = requiredVerticalSpaceForTree;
		this.rootRadius = rootRadius;
		this.rootReplaceable = rootReplaceable;
		this.rootStateProvider = rootStateProvider;
		this.rootPlacementAttempts = rootPlacementAttempts;
		this.maxRootColumnHeight = maxRootColumnHeight;
		this.hangingRootRadius = hangingRootRadius;
		this.hangingRootVerticalSpan = hangingRootVerticalSpan;
		this.hangingRootStateProvider = hangingRootStateProvider;
		this.hangingRootPlacementAttempts = hangingRootPlacementAttempts;
		this.allowedVerticalWaterForTree = allowedVerticalWaterForTree;
	}
}

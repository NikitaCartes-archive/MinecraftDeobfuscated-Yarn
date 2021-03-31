package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class VegetationPatchFeatureConfig implements FeatureConfig {
	public static final Codec<VegetationPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("replaceable").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.replaceable),
					BlockStateProvider.TYPE_CODEC.fieldOf("ground_state").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.groundState),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("vegetation_feature").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.vegetationFeature),
					VerticalSurfaceType.CODEC.fieldOf("surface").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.surface),
					IntProvider.createValidatingCodec(1, 128).fieldOf("depth").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.depth),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("extra_bottom_block_chance")
						.forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.extraBottomBlockChance),
					Codec.intRange(1, 256).fieldOf("vertical_range").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.verticalRange),
					Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.vegetationChance),
					IntProvider.VALUE_CODEC.fieldOf("xz_radius").forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.horizontalRadius),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("extra_edge_column_chance")
						.forGetter(vegetationPatchFeatureConfig -> vegetationPatchFeatureConfig.extraEdgeColumnChance)
				)
				.apply(instance, VegetationPatchFeatureConfig::new)
	);
	public final Identifier replaceable;
	public final BlockStateProvider groundState;
	public final Supplier<ConfiguredFeature<?, ?>> vegetationFeature;
	public final VerticalSurfaceType surface;
	public final IntProvider depth;
	public final float extraBottomBlockChance;
	public final int verticalRange;
	public final float vegetationChance;
	public final IntProvider horizontalRadius;
	public final float extraEdgeColumnChance;

	public VegetationPatchFeatureConfig(
		Identifier replaceable,
		BlockStateProvider groundState,
		Supplier<ConfiguredFeature<?, ?>> vegetationFeature,
		VerticalSurfaceType surface,
		IntProvider depth,
		float extraBottomBlockChance,
		int verticalRange,
		float vegetationChance,
		IntProvider horizontalRadius,
		float extraEdgeColumnChance
	) {
		this.replaceable = replaceable;
		this.groundState = groundState;
		this.vegetationFeature = vegetationFeature;
		this.surface = surface;
		this.depth = depth;
		this.extraBottomBlockChance = extraBottomBlockChance;
		this.verticalRange = verticalRange;
		this.vegetationChance = vegetationChance;
		this.horizontalRadius = horizontalRadius;
		this.extraEdgeColumnChance = extraEdgeColumnChance;
	}
}

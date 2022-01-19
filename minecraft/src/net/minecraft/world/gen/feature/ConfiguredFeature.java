package net.minecraft.world.gen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.slf4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>> {
	public static final Codec<ConfiguredFeature<?, ?>> CODEC = Registry.FEATURE
		.getCodec()
		.dispatch(configuredFeature -> configuredFeature.feature, Feature::getCodec);
	public static final Codec<Supplier<ConfiguredFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_FEATURE_KEY, CODEC);
	public static final Codec<List<Supplier<ConfiguredFeature<?, ?>>>> LIST_CODEC = RegistryElementCodec.method_31194(Registry.CONFIGURED_FEATURE_KEY, CODEC);
	private static final Logger LOGGER = LogUtils.getLogger();
	public final F feature;
	public final FC config;

	public ConfiguredFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
	}

	public F getFeature() {
		return this.feature;
	}

	public FC getConfig() {
		return this.config;
	}

	/**
	 * Attaches placement modifiers to this configured feature. Attached
	 * placement modifiers will be applied before this configured feature is
	 * generated.
	 * 
	 * @see #withPlacement(PlacementModifier...)
	 * @see PlacedFeature#generate(StructureWorldAccess, ChunkGenerator, Random, BlockPos)
	 */
	public PlacedFeature withPlacement(List<PlacementModifier> modifiers) {
		return new PlacedFeature(() -> this, modifiers);
	}

	/**
	 * Attaches placement modifiers to this configured feature. Attached
	 * placement modifiers will be applied before this configured feature is
	 * generated.
	 * 
	 * @see #withPlacement(List)
	 * @see PlacedFeature#generate(StructureWorldAccess, ChunkGenerator, Random, BlockPos)
	 */
	public PlacedFeature withPlacement(PlacementModifier... modifiers) {
		return this.withPlacement(List.of(modifiers));
	}

	public PlacedFeature withWouldSurviveFilter(Block block) {
		return this.withBlockPredicateFilter(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN));
	}

	public PlacedFeature withInAirFilter() {
		return this.withBlockPredicateFilter(BlockPredicate.matchingBlock(Blocks.AIR, BlockPos.ORIGIN));
	}

	public PlacedFeature withBlockPredicateFilter(BlockPredicate predicate) {
		return this.withPlacement(BlockFilterPlacementModifier.of(predicate));
	}

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos origin) {
		return world.isValidForSetBlock(origin)
			? this.feature.generate(new FeatureContext<>(Optional.empty(), world, chunkGenerator, random, origin, this.config))
			: false;
	}

	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(Stream.of(this), this.config.getDecoratedFeatures());
	}

	public String toString() {
		return (String)BuiltinRegistries.CONFIGURED_FEATURE.getKey(this).map(Objects::toString).orElseGet(() -> CODEC.encodeStart(JsonOps.INSTANCE, this).toString());
	}
}

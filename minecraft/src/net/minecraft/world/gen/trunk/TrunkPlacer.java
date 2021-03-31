package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public abstract class TrunkPlacer {
	public static final Codec<TrunkPlacer> TYPE_CODEC = Registry.TRUNK_PLACER_TYPE.dispatch(TrunkPlacer::getType, TrunkPlacerType::getCodec);
	private static final int field_31528 = 32;
	private static final int field_31529 = 24;
	public static final int field_31530 = 80;
	protected final int baseHeight;
	protected final int firstRandomHeight;
	protected final int secondRandomHeight;

	protected static <P extends TrunkPlacer> P3<Mu<P>, Integer, Integer, Integer> fillTrunkPlacerFields(Instance<P> instance) {
		return instance.group(
			Codec.intRange(0, 32).fieldOf("base_height").forGetter(trunkPlacer -> trunkPlacer.baseHeight),
			Codec.intRange(0, 24).fieldOf("height_rand_a").forGetter(trunkPlacer -> trunkPlacer.firstRandomHeight),
			Codec.intRange(0, 24).fieldOf("height_rand_b").forGetter(trunkPlacer -> trunkPlacer.secondRandomHeight)
		);
	}

	public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		this.baseHeight = baseHeight;
		this.firstRandomHeight = firstRandomHeight;
		this.secondRandomHeight = secondRandomHeight;
	}

	protected abstract TrunkPlacerType<?> getType();

	/**
	 * Generates the trunk blocks and return a list of tree nodes to place foliage around
	 */
	public abstract List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	);

	public int getHeight(Random random) {
		return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
	}

	private static boolean canGenerate(TestableWorld world, BlockPos pos) {
		return world.testBlockState(pos, state -> Feature.isSoil(state) && !state.isOf(Blocks.GRASS_BLOCK) && !state.isOf(Blocks.MYCELIUM));
	}

	protected static void setToDirt(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		if (treeFeatureConfig.forceDirt || !canGenerate(testableWorld, blockPos)) {
			biConsumer.accept(blockPos, treeFeatureConfig.dirtProvider.getBlockState(random, blockPos));
		}
	}

	protected static boolean method_35375(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		return getAndSetState(testableWorld, biConsumer, random, blockPos, treeFeatureConfig, Function.identity());
	}

	protected static boolean getAndSetState(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		BlockPos blockPos,
		TreeFeatureConfig treeFeatureConfig,
		Function<BlockState, BlockState> function
	) {
		if (TreeFeature.canReplace(testableWorld, blockPos)) {
			biConsumer.accept(blockPos, function.apply(treeFeatureConfig.trunkProvider.getBlockState(random, blockPos)));
			return true;
		} else {
			return false;
		}
	}

	protected static void trySetState(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos.Mutable mutable, TreeFeatureConfig treeFeatureConfig
	) {
		if (TreeFeature.canTreeReplace(testableWorld, mutable)) {
			method_35375(testableWorld, biConsumer, random, mutable, treeFeatureConfig);
		}
	}
}

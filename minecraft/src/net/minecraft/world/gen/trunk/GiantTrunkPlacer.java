package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class GiantTrunkPlacer extends TrunkPlacer {
	public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance).apply(instance, GiantTrunkPlacer::new)
	);

	public GiantTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.GIANT_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		BlockPos blockPos2 = blockPos.down();
		setToDirt(testableWorld, biConsumer, random, blockPos2, treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.east(), treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.south(), treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.south().east(), treeFeatureConfig);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < i; j++) {
			setLog(testableWorld, biConsumer, random, mutable, treeFeatureConfig, blockPos, 0, j, 0);
			if (j < i - 1) {
				setLog(testableWorld, biConsumer, random, mutable, treeFeatureConfig, blockPos, 1, j, 0);
				setLog(testableWorld, biConsumer, random, mutable, treeFeatureConfig, blockPos, 1, j, 1);
				setLog(testableWorld, biConsumer, random, mutable, treeFeatureConfig, blockPos, 0, j, 1);
			}
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(blockPos.up(i), 0, true));
	}

	private static void setLog(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		BlockPos.Mutable mutable,
		TreeFeatureConfig treeFeatureConfig,
		BlockPos blockPos,
		int i,
		int j,
		int k
	) {
		mutable.set(blockPos, i, j, k);
		trySetState(testableWorld, biConsumer, random, mutable, treeFeatureConfig);
	}
}

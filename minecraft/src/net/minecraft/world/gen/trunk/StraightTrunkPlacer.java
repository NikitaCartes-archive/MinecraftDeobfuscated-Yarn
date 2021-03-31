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

public class StraightTrunkPlacer extends TrunkPlacer {
	public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance).apply(instance, StraightTrunkPlacer::new)
	);

	public StraightTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		setToDirt(testableWorld, biConsumer, random, blockPos.down(), treeFeatureConfig);

		for (int j = 0; j < i; j++) {
			method_35375(testableWorld, biConsumer, random, blockPos.up(j), treeFeatureConfig);
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(blockPos.up(i), 0, false));
	}
}

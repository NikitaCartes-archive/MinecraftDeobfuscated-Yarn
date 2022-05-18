package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	) {
		setToDirt(world, replacer, random, startPos.down(), config);

		for (int i = 0; i < height; i++) {
			this.getAndSetState(world, replacer, random, startPos.up(i), config);
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false));
	}
}

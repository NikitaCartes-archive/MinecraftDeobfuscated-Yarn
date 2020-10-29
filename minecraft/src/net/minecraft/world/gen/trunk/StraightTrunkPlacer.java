package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class StraightTrunkPlacer extends TrunkPlacer {
	public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> method_28904(instance).apply(instance, StraightTrunkPlacer::new));

	public StraightTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config
	) {
		setToDirt(world, pos.down());

		for (int i = 0; i < trunkHeight; i++) {
			getAndSetState(world, random, pos.up(i), placedStates, box, config);
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(pos.up(trunkHeight), 0, false));
	}
}

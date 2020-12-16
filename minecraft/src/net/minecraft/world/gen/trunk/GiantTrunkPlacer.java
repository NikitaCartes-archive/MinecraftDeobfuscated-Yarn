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

public class GiantTrunkPlacer extends TrunkPlacer {
	public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> method_28904(instance).apply(instance, GiantTrunkPlacer::new));

	public GiantTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.GIANT_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config
	) {
		BlockPos blockPos = pos.down();
		setToDirt(world, blockPos);
		setToDirt(world, blockPos.east());
		setToDirt(world, blockPos.south());
		setToDirt(world, blockPos.south().east());
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < trunkHeight; i++) {
			setLog(world, random, mutable, placedStates, box, config, pos, 0, i, 0);
			if (i < trunkHeight - 1) {
				setLog(world, random, mutable, placedStates, box, config, pos, 1, i, 0);
				setLog(world, random, mutable, placedStates, box, config, pos, 1, i, 1);
				setLog(world, random, mutable, placedStates, box, config, pos, 0, i, 1);
			}
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(pos.up(trunkHeight), 0, true));
	}

	private static void setLog(
		ModifiableTestableWorld world,
		Random random,
		BlockPos.Mutable mutable,
		Set<BlockPos> placedStates,
		BlockBox box,
		TreeFeatureConfig config,
		BlockPos pos,
		int x,
		int y,
		int z
	) {
		mutable.set(pos, x, y, z);
		trySetState(world, random, mutable, placedStates, box, config);
	}
}

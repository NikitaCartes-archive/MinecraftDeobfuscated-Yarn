package net.minecraft.world.gen.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class GiantTrunkPlacer extends TrunkPlacer {
	public GiantTrunkPlacer(int i, int j, int k) {
		this(i, j, k, TrunkPlacerType.GIANT_TRUNK_PLACER);
	}

	public GiantTrunkPlacer(int i, int j, int k, TrunkPlacerType<? extends GiantTrunkPlacer> trunkPlacerType) {
		super(i, j, k, trunkPlacerType);
	}

	public <T> GiantTrunkPlacer(Dynamic<T> dynamic) {
		this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		BlockPos blockPos = pos.down();
		method_27400(world, blockPos);
		method_27400(world, blockPos.east());
		method_27400(world, blockPos.south());
		method_27400(world, blockPos.south().east());
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < trunkHeight; i++) {
			method_27399(world, random, mutable, set, blockBox, treeFeatureConfig, pos, 0, i, 0);
			if (i < trunkHeight - 1) {
				method_27399(world, random, mutable, set, blockBox, treeFeatureConfig, pos, 1, i, 0);
				method_27399(world, random, mutable, set, blockBox, treeFeatureConfig, pos, 1, i, 1);
				method_27399(world, random, mutable, set, blockBox, treeFeatureConfig, pos, 0, i, 1);
			}
		}

		return ImmutableList.of(new FoliagePlacer.TreeNode(pos.up(trunkHeight), 0, true));
	}

	private static void method_27399(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos.Mutable mutable,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig,
		BlockPos blockPos,
		int i,
		int j,
		int k
	) {
		mutable.set(blockPos, i, j, k);
		method_27401(modifiableTestableWorld, random, mutable, set, blockBox, treeFeatureConfig);
	}
}

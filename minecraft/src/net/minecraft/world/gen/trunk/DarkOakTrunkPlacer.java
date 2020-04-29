package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class DarkOakTrunkPlacer extends TrunkPlacer {
	public DarkOakTrunkPlacer(int i, int j, int k) {
		this(i, j, k, TrunkPlacerType.DARK_OAK_TRUNK_PLACER);
	}

	public DarkOakTrunkPlacer(int i, int j, int k, TrunkPlacerType<? extends DarkOakTrunkPlacer> trunkPlacerType) {
		super(i, j, k, trunkPlacerType);
	}

	public <T> DarkOakTrunkPlacer(Dynamic<T> dynamic) {
		this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		BlockPos blockPos = pos.down();
		method_27400(world, blockPos);
		method_27400(world, blockPos.east());
		method_27400(world, blockPos.south());
		method_27400(world, blockPos.south().east());
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int i = trunkHeight - random.nextInt(4);
		int j = 2 - random.nextInt(3);
		int k = pos.getX();
		int l = pos.getY();
		int m = pos.getZ();
		int n = k;
		int o = m;
		int p = l + trunkHeight - 1;

		for (int q = 0; q < trunkHeight; q++) {
			if (q >= i && j > 0) {
				n += direction.getOffsetX();
				o += direction.getOffsetZ();
				j--;
			}

			int r = l + q;
			BlockPos blockPos2 = new BlockPos(n, r, o);
			if (AbstractTreeFeature.isAirOrLeaves(world, blockPos2)) {
				method_27402(world, random, blockPos2, set, blockBox, treeFeatureConfig);
				method_27402(world, random, blockPos2.east(), set, blockBox, treeFeatureConfig);
				method_27402(world, random, blockPos2.south(), set, blockBox, treeFeatureConfig);
				method_27402(world, random, blockPos2.east().south(), set, blockBox, treeFeatureConfig);
			}
		}

		list.add(new FoliagePlacer.TreeNode(new BlockPos(n, p, o), 0, true));

		for (int q = -1; q <= 2; q++) {
			for (int r = -1; r <= 2; r++) {
				if ((q < 0 || q > 1 || r < 0 || r > 1) && random.nextInt(3) <= 0) {
					int s = random.nextInt(3) + 2;

					for (int t = 0; t < s; t++) {
						method_27402(world, random, new BlockPos(k + q, p - t - 1, m + r), set, blockBox, treeFeatureConfig);
					}

					list.add(new FoliagePlacer.TreeNode(new BlockPos(n + q, p, o + r), 0, false));
				}
			}
		}

		return list;
	}
}

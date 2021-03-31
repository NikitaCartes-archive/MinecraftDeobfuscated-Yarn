package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class DarkOakTrunkPlacer extends TrunkPlacer {
	public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance).apply(instance, DarkOakTrunkPlacer::new)
	);

	public DarkOakTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		BlockPos blockPos2 = blockPos.down();
		setToDirt(testableWorld, biConsumer, random, blockPos2, treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.east(), treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.south(), treeFeatureConfig);
		setToDirt(testableWorld, biConsumer, random, blockPos2.south().east(), treeFeatureConfig);
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int j = i - random.nextInt(4);
		int k = 2 - random.nextInt(3);
		int l = blockPos.getX();
		int m = blockPos.getY();
		int n = blockPos.getZ();
		int o = l;
		int p = n;
		int q = m + i - 1;

		for (int r = 0; r < i; r++) {
			if (r >= j && k > 0) {
				o += direction.getOffsetX();
				p += direction.getOffsetZ();
				k--;
			}

			int s = m + r;
			BlockPos blockPos3 = new BlockPos(o, s, p);
			if (TreeFeature.isAirOrLeaves(testableWorld, blockPos3)) {
				method_35375(testableWorld, biConsumer, random, blockPos3, treeFeatureConfig);
				method_35375(testableWorld, biConsumer, random, blockPos3.east(), treeFeatureConfig);
				method_35375(testableWorld, biConsumer, random, blockPos3.south(), treeFeatureConfig);
				method_35375(testableWorld, biConsumer, random, blockPos3.east().south(), treeFeatureConfig);
			}
		}

		list.add(new FoliagePlacer.TreeNode(new BlockPos(o, q, p), 0, true));

		for (int r = -1; r <= 2; r++) {
			for (int s = -1; s <= 2; s++) {
				if ((r < 0 || r > 1 || s < 0 || s > 1) && random.nextInt(3) <= 0) {
					int t = random.nextInt(3) + 2;

					for (int u = 0; u < t; u++) {
						method_35375(testableWorld, biConsumer, random, new BlockPos(l + r, q - u - 1, n + s), treeFeatureConfig);
					}

					list.add(new FoliagePlacer.TreeNode(new BlockPos(o + r, q, p + s), 0, false));
				}
			}
		}

		return list;
	}
}

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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class ForkingTrunkPlacer extends TrunkPlacer {
	public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillTrunkPlacerFields(instance).apply(instance, ForkingTrunkPlacer::new)
	);

	public ForkingTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.FORKING_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig
	) {
		setToDirt(testableWorld, biConsumer, random, blockPos.down(), treeFeatureConfig);
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int j = i - random.nextInt(4) - 1;
		int k = 3 - random.nextInt(3);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int l = blockPos.getX();
		int m = blockPos.getZ();
		int n = 0;

		for (int o = 0; o < i; o++) {
			int p = blockPos.getY() + o;
			if (o >= j && k > 0) {
				l += direction.getOffsetX();
				m += direction.getOffsetZ();
				k--;
			}

			if (method_35375(testableWorld, biConsumer, random, mutable.set(l, p, m), treeFeatureConfig)) {
				n = p + 1;
			}
		}

		list.add(new FoliagePlacer.TreeNode(new BlockPos(l, n, m), 1, false));
		l = blockPos.getX();
		m = blockPos.getZ();
		Direction direction2 = Direction.Type.HORIZONTAL.random(random);
		if (direction2 != direction) {
			int px = j - random.nextInt(2) - 1;
			int q = 1 + random.nextInt(3);
			n = 0;

			for (int r = px; r < i && q > 0; q--) {
				if (r >= 1) {
					int s = blockPos.getY() + r;
					l += direction2.getOffsetX();
					m += direction2.getOffsetZ();
					if (method_35375(testableWorld, biConsumer, random, mutable.set(l, s, m), treeFeatureConfig)) {
						n = s + 1;
					}
				}

				r++;
			}

			if (n > 1) {
				list.add(new FoliagePlacer.TreeNode(new BlockPos(l, n, m), 0, false));
			}
		}

		return list;
	}
}

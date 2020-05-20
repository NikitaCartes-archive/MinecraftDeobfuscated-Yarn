package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class ForkingTrunkPlacer extends TrunkPlacer {
	public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> method_28904(instance).apply(instance, ForkingTrunkPlacer::new));

	public ForkingTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> method_28903() {
		return TrunkPlacerType.FORKING_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		method_27400(world, pos.down());
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int i = trunkHeight - random.nextInt(4) - 1;
		int j = 3 - random.nextInt(3);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int k = pos.getX();
		int l = pos.getZ();
		int m = 0;

		for (int n = 0; n < trunkHeight; n++) {
			int o = pos.getY() + n;
			if (n >= i && j > 0) {
				k += direction.getOffsetX();
				l += direction.getOffsetZ();
				j--;
			}

			if (method_27402(world, random, mutable.set(k, o, l), set, blockBox, treeFeatureConfig)) {
				m = o + 1;
			}
		}

		list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 1, false));
		k = pos.getX();
		l = pos.getZ();
		Direction direction2 = Direction.Type.HORIZONTAL.random(random);
		if (direction2 != direction) {
			int ox = i - random.nextInt(2) - 1;
			int p = 1 + random.nextInt(3);
			m = 0;

			for (int q = ox; q < trunkHeight && p > 0; p--) {
				if (q >= 1) {
					int r = pos.getY() + q;
					k += direction2.getOffsetX();
					l += direction2.getOffsetZ();
					if (method_27402(world, random, mutable.set(k, r, l), set, blockBox, treeFeatureConfig)) {
						m = r + 1;
					}
				}

				q++;
			}

			if (m > 1) {
				list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 0, false));
			}
		}

		return list;
	}
}

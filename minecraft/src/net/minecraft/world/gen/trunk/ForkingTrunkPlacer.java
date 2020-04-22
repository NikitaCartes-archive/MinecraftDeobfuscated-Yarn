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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class ForkingTrunkPlacer extends TrunkPlacer {
	public ForkingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		super(baseHeight, firstRandomHeight, secondRandomHeight, TrunkPlacerType.FORKING_TRUNK_PLACER);
	}

	public <T> ForkingTrunkPlacer(Dynamic<T> data) {
		this(data.get("base_height").asInt(0), data.get("height_rand_a").asInt(0), data.get("height_rand_b").asInt(0));
	}

	@Override
	public List<FoliagePlacer.class_5208> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		method_27400(world, pos.down());
		List<FoliagePlacer.class_5208> list = Lists.<FoliagePlacer.class_5208>newArrayList();
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

		list.add(new FoliagePlacer.class_5208(new BlockPos(k, m, l), 1, false));
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
				list.add(new FoliagePlacer.class_5208(new BlockPos(k, m, l), 0, false));
			}
		}

		return list;
	}
}

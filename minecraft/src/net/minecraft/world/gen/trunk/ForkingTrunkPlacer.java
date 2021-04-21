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
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	) {
		setToDirt(world, replacer, random, startPos.down(), config);
		List<FoliagePlacer.TreeNode> list = Lists.<FoliagePlacer.TreeNode>newArrayList();
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int i = height - random.nextInt(4) - 1;
		int j = 3 - random.nextInt(3);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int k = startPos.getX();
		int l = startPos.getZ();
		int m = 0;

		for (int n = 0; n < height; n++) {
			int o = startPos.getY() + n;
			if (n >= i && j > 0) {
				k += direction.getOffsetX();
				l += direction.getOffsetZ();
				j--;
			}

			if (getAndSetState(world, replacer, random, mutable.set(k, o, l), config)) {
				m = o + 1;
			}
		}

		list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 1, false));
		k = startPos.getX();
		l = startPos.getZ();
		Direction direction2 = Direction.Type.HORIZONTAL.random(random);
		if (direction2 != direction) {
			int ox = i - random.nextInt(2) - 1;
			int p = 1 + random.nextInt(3);
			m = 0;

			for (int q = ox; q < height && p > 0; p--) {
				if (q >= 1) {
					int r = startPos.getY() + q;
					k += direction2.getOffsetX();
					l += direction2.getOffsetZ();
					if (getAndSetState(world, replacer, random, mutable.set(k, r, l), config)) {
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

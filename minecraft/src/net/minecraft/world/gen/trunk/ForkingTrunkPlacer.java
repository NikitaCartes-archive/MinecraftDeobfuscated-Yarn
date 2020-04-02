package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class ForkingTrunkPlacer extends TrunkPlacer {
	public ForkingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
		super(baseHeight, firstRandomHeight, secondRandomHeight, TrunkPlacerType.FORKING_TRUNK_PLACER);
	}

	public <T> ForkingTrunkPlacer(Dynamic<T> data) {
		this(data.get("base_height").asInt(0), data.get("height_rand_a").asInt(0), data.get("height_rand_b").asInt(0));
	}

	@Override
	public Map<BlockPos, Integer> generate(
		ModifiableTestableWorld world,
		Random random,
		int trunkHeight,
		BlockPos pos,
		int foliageRadius,
		Set<BlockPos> logs,
		BlockBox box,
		BranchedTreeFeatureConfig config
	) {
		Map<BlockPos, Integer> map = new Object2ObjectLinkedOpenHashMap<>();
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

			if (AbstractTreeFeature.setLogBlockState(world, random, mutable.set(k, o, l), logs, box, config)) {
				m = o + 1;
			}
		}

		map.put(new BlockPos(k, m, l), foliageRadius + 1);
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
					if (AbstractTreeFeature.setLogBlockState(world, random, mutable.set(k, r, l), logs, box, config)) {
						m = r + 1;
					}
				}

				q++;
			}

			if (m > 1) {
				map.put(new BlockPos(k, m, l), foliageRadius);
			}
		}

		return map;
	}
}

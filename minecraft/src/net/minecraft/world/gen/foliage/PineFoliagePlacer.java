package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class PineFoliagePlacer extends FoliagePlacer {
	public PineFoliagePlacer(int i, int j) {
		super(i, j, FoliagePlacerType.PINE_FOLIAGE_PLACER);
	}

	public <T> PineFoliagePlacer(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void method_23448(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig,
		int i,
		int j,
		int k,
		BlockPos blockPos,
		Set<BlockPos> set
	) {
		int l = 0;

		for (int m = i; m >= j; m--) {
			this.method_23449(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, blockPos, m, l, set);
			if (l >= 1 && m == j + 1) {
				l--;
			} else if (l < k) {
				l++;
			}
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
		return this.radius + random.nextInt(this.randomRadius + 1) + random.nextInt(j - i + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && m > 0;
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		return l <= 1 ? 0 : 2;
	}
}
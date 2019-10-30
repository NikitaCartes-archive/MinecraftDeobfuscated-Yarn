package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class SpruceFoliagePlacer extends FoliagePlacer {
	public SpruceFoliagePlacer(int i, int j) {
		super(i, j, FoliagePlacerType.SPRUCE_FOLIAGE_PLACER);
	}

	public <T> SpruceFoliagePlacer(Dynamic<T> dynamic) {
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
		int l = random.nextInt(2);
		int m = 1;
		int n = 0;

		for (int o = i; o >= j; o--) {
			this.method_23449(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, blockPos, o, l, set);
			if (l >= m) {
				l = n;
				n = 1;
				m = Math.min(m + 1, k);
			} else {
				l++;
			}
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
		return this.radius + random.nextInt(this.randomRadius + 1);
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

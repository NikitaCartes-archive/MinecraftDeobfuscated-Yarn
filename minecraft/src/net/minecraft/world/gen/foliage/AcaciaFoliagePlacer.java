package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.NormalTreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public AcaciaFoliagePlacer(int i, int j) {
		super(i, j, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
	}

	public <T> AcaciaFoliagePlacer(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void method_23448(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		NormalTreeFeatureConfig normalTreeFeatureConfig,
		int i,
		int j,
		int k,
		BlockPos blockPos,
		Set<BlockPos> set
	) {
		normalTreeFeatureConfig.foliagePlacer.method_23449(modifiableTestableWorld, random, normalTreeFeatureConfig, i, blockPos, 0, k, set);
		normalTreeFeatureConfig.foliagePlacer.method_23449(modifiableTestableWorld, random, normalTreeFeatureConfig, i, blockPos, 1, 1, set);
		BlockPos blockPos2 = blockPos.up();

		for (int l = 2; l <= k - 1; l++) {
			this.method_23450(modifiableTestableWorld, random, blockPos2.east(l), normalTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.west(l), normalTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.south(l), normalTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.north(l), normalTreeFeatureConfig, set);
		}
	}

	@Override
	public int method_23452(Random random, int i, int j, NormalTreeFeatureConfig normalTreeFeatureConfig) {
		return this.field_21296 + random.nextInt(this.field_21297 + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && m > 0;
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		if (l == 0) {
			return 0;
		} else {
			return l >= 1 + j - 2 ? k : 1;
		}
	}
}

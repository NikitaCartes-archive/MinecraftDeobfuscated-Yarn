package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	public BlobFoliagePlacer(int radius, int radiusRandom) {
		super(radius, radiusRandom, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	}

	public <T> BlobFoliagePlacer(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void generate(
		ModifiableTestableWorld world, Random random, BranchedTreeFeatureConfig config, int i, int j, int k, BlockPos pos, Set<BlockPos> positions
	) {
		for (int l = i; l >= j; l--) {
			int m = Math.max(k - 1 - (l - i) / 2, 0);
			this.generate(world, random, config, i, pos, l, m, positions);
		}
	}

	@Override
	public int getRadius(Random random, int i, int j, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && (random.nextInt(2) == 0 || k == i);
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		return l == 0 ? 0 : 1;
	}
}

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
		ModifiableTestableWorld world,
		Random random,
		BranchedTreeFeatureConfig config,
		int baseHeight,
		int trunkHeight,
		int radius,
		BlockPos pos,
		Set<BlockPos> leaves
	) {
		for (int i = baseHeight; i >= trunkHeight; i--) {
			int j = Math.max(radius - 1 - (i - baseHeight) / 2, 0);
			this.generate(world, random, config, baseHeight, pos, i, j, leaves);
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, int trunkHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int x, int y, int z, int radius) {
		return Math.abs(x) == radius && Math.abs(z) == radius && (random.nextInt(2) == 0 || y == baseHeight);
	}

	@Override
	public int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius, int currentTreeHeight) {
		return currentTreeHeight == 0 ? 0 : 1;
	}

	public static BlobFoliagePlacer method_26653(Random random) {
		return new BlobFoliagePlacer(random.nextInt(10) + 1, random.nextInt(5));
	}
}

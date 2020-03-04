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
		int i = random.nextInt(2);
		int j = 1;
		int k = 0;

		for (int l = baseHeight; l >= trunkHeight; l--) {
			this.generate(world, random, config, baseHeight, pos, l, i, leaves);
			if (i >= j) {
				i = k;
				k = 1;
				j = Math.min(j + 1, radius);
			} else {
				i++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, int trunkHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int x, int y, int z, int radius) {
		return Math.abs(x) == radius && Math.abs(z) == radius && radius > 0;
	}

	@Override
	public int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius, int currentTreeHeight) {
		return currentTreeHeight <= 1 ? 0 : 2;
	}
}

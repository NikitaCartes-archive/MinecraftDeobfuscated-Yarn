package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public AcaciaFoliagePlacer(int i, int j) {
		super(i, j, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
	}

	public <T> AcaciaFoliagePlacer(Dynamic<T> dynamic) {
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
		config.foliagePlacer.generate(world, random, config, baseHeight, pos, 0, radius, leaves);
		config.foliagePlacer.generate(world, random, config, baseHeight, pos, 1, 1, leaves);
		BlockPos blockPos = pos.up();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				this.placeLeaves(world, random, blockPos.add(i, 0, j), config, leaves);
			}
		}

		for (int i = 2; i <= radius - 1; i++) {
			this.placeLeaves(world, random, blockPos.east(i), config, leaves);
			this.placeLeaves(world, random, blockPos.west(i), config, leaves);
			this.placeLeaves(world, random, blockPos.south(i), config, leaves);
			this.placeLeaves(world, random, blockPos.north(i), config, leaves);
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
		return currentTreeHeight == 0 ? 0 : 2;
	}

	public static AcaciaFoliagePlacer method_26652(Random random) {
		return new AcaciaFoliagePlacer(random.nextInt(10) + 1, random.nextInt(5));
	}
}

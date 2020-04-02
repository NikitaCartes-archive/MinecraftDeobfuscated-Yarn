package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public AcaciaFoliagePlacer(int radius, int randomRadius, int offset, int randomOffset) {
		super(radius, randomRadius, offset, randomOffset, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
	}

	public <T> AcaciaFoliagePlacer(Dynamic<T> data) {
		this(data.get("radius").asInt(0), data.get("radius_random").asInt(0), data.get("offset").asInt(0), data.get("offset_random").asInt(0));
	}

	@Override
	public void generate(
		ModifiableTestableWorld world,
		Random random,
		BranchedTreeFeatureConfig config,
		int trunkHeight,
		BlockPos pos,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves
	) {
		int i = this.offset + random.nextInt(this.randomOffset + 1);
		config.foliagePlacer.generate(world, random, config, pos, foliageHeight, i - 1, radius, leaves);
		config.foliagePlacer.generate(world, random, config, pos, foliageHeight, i, 1, leaves);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				this.placeLeaves(world, random, pos.add(j, 0, k), config, leaves);
			}
		}

		for (int j = 2; j <= radius - 1; j++) {
			this.placeLeaves(world, random, pos.up(i).east(j), config, leaves);
			this.placeLeaves(world, random, pos.up(i).west(j), config, leaves);
			this.placeLeaves(world, random, pos.up(i).south(j), config, leaves);
			this.placeLeaves(world, random, pos.up(i).north(j), config, leaves);
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight, BranchedTreeFeatureConfig config) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight) {
		return 0;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, int radius) {
		return Math.abs(dx) == radius && Math.abs(dz) == radius && radius > 0;
	}

	@Override
	public int getRadiusForPlacement(int trunkHeight, int baseHeight, int radius) {
		return radius == 0 ? 0 : 2;
	}
}

package net.minecraft.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class DarkOakFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
		instance -> fillFoliagePlacerFields(instance).apply(instance, DarkOakFoliagePlacer::new)
	);

	public DarkOakFoliagePlacer(IntProvider intProvider, IntProvider intProvider2) {
		super(intProvider, intProvider2);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		TestableWorld world,
		FoliagePlacer.BlockPlacer placer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		BlockPos blockPos = treeNode.getCenter().up(offset);
		boolean bl = treeNode.isGiantTrunk();
		if (bl) {
			this.generateSquare(world, placer, random, config, blockPos, radius + 2, -1, bl);
			this.generateSquare(world, placer, random, config, blockPos, radius + 3, 0, bl);
			this.generateSquare(world, placer, random, config, blockPos, radius + 2, 1, bl);
			if (random.nextBoolean()) {
				this.generateSquare(world, placer, random, config, blockPos, radius, 2, bl);
			}
		} else {
			this.generateSquare(world, placer, random, config, blockPos, radius + 2, -1, bl);
			this.generateSquare(world, placer, random, config, blockPos, radius + 1, 0, bl);
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 4;
	}

	@Override
	protected boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return y != 0 || !giantTrunk || dx != -radius && dx < radius || dz != -radius && dz < radius
			? super.isPositionInvalid(random, dx, y, dz, radius, giantTrunk)
			: true;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		if (y == -1 && !giantTrunk) {
			return dx == radius && dz == radius;
		} else {
			return y == 1 ? dx + dz > radius * 2 - 2 : false;
		}
	}
}

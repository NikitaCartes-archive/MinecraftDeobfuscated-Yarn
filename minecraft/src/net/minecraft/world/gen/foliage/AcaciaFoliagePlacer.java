package net.minecraft.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
		instance -> fillFoliagePlacerFields(instance).apply(instance, AcaciaFoliagePlacer::new)
	);

	public AcaciaFoliagePlacer(IntProvider intProvider, IntProvider intProvider2) {
		super(intProvider, intProvider2);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
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
		boolean bl = treeNode.isGiantTrunk();
		BlockPos blockPos = treeNode.getCenter().up(offset);
		this.generateSquare(world, placer, random, config, blockPos, radius + treeNode.getFoliageRadius(), -1 - foliageHeight, bl);
		this.generateSquare(world, placer, random, config, blockPos, radius - 1, -foliageHeight, bl);
		this.generateSquare(world, placer, random, config, blockPos, radius + treeNode.getFoliageRadius() - 1, 0, bl);
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 0;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return y == 0 ? (dx > 1 || dz > 1) && dx != 0 && dz != 0 : dx == radius && dz == radius && radius > 0;
	}
}

package net.minecraft.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class PineFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<PineFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
		instance -> fillFoliagePlacerFields(instance)
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("height").forGetter(placer -> placer.height))
				.apply(instance, PineFoliagePlacer::new)
	);
	private final IntProvider height;

	public PineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.PINE_FOLIAGE_PLACER;
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
		int i = 0;

		for (int j = offset; j >= offset - foliageHeight; j--) {
			this.generateSquare(world, placer, random, config, treeNode.getCenter(), i, j, treeNode.isGiantTrunk());
			if (i >= 1 && j == offset - foliageHeight + 1) {
				i--;
			} else if (i < radius + treeNode.getFoliageRadius()) {
				i++;
			}
		}
	}

	@Override
	public int getRandomRadius(Random random, int baseHeight) {
		return super.getRandomRadius(random, baseHeight) + random.nextInt(Math.max(baseHeight + 1, 1));
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height.get(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && radius > 0;
	}
}

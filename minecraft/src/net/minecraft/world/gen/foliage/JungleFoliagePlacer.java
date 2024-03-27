package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class JungleFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
		instance -> fillFoliagePlacerFields(instance)
				.and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height))
				.apply(instance, JungleFoliagePlacer::new)
	);
	protected final int height;

	public JungleFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
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
		int i = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);

		for (int j = offset; j >= offset - i; j--) {
			int k = radius + treeNode.getFoliageRadius() + 1 - j;
			this.generateSquare(world, placer, random, config, treeNode.getCenter(), k, j, treeNode.isGiantTrunk());
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx + dz >= 7 ? true : dx * dx + dz * dz > radius * radius;
	}
}

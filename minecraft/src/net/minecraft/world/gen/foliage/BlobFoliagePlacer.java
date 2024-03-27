package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	public static final MapCodec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> createCodec(instance).apply(instance, BlobFoliagePlacer::new));
	protected final int height;

	protected static <P extends BlobFoliagePlacer> P3<Mu<P>, IntProvider, IntProvider, Integer> createCodec(Instance<P> builder) {
		return fillFoliagePlacerFields(builder).and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height));
	}

	public BlobFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
		super(radius, offset);
		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
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
		for (int i = offset; i >= offset - foliageHeight; i--) {
			int j = Math.max(radius + treeNode.getFoliageRadius() - 1 - i / 2, 0);
			this.generateSquare(world, placer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx == radius && dz == radius && (random.nextInt(2) == 0 || y == 0);
	}
}

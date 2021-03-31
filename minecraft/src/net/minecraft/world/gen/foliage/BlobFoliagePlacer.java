package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class BlobFoliagePlacer extends FoliagePlacer {
	public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> createCodec(instance).apply(instance, BlobFoliagePlacer::new));
	protected final int height;

	protected static <P extends BlobFoliagePlacer> P3<Mu<P>, IntProvider, IntProvider, Integer> createCodec(Instance<P> builder) {
		return fillFoliagePlacerFields(builder).and(Codec.intRange(0, 16).fieldOf("height").forGetter(blobFoliagePlacer -> blobFoliagePlacer.height));
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
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.TreeNode treeNode,
		int radius,
		int j,
		int offset
	) {
		for (int k = offset; k >= offset - radius; k--) {
			int l = Math.max(j + treeNode.getFoliageRadius() - 1 - k / 2, 0);
			this.generateSquare(testableWorld, biConsumer, random, treeFeatureConfig, treeNode.getCenter(), l, k, treeNode.isGiantTrunk());
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

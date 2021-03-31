package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer {
	public static final Codec<FoliagePlacer> TYPE_CODEC = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
	protected final IntProvider radius;
	protected final IntProvider offset;

	protected static <P extends FoliagePlacer> P2<Mu<P>, IntProvider, IntProvider> fillFoliagePlacerFields(Instance<P> instance) {
		return instance.group(
			IntProvider.createValidatingCodec(0, 16).fieldOf("radius").forGetter(foliagePlacer -> foliagePlacer.radius),
			IntProvider.createValidatingCodec(0, 16).fieldOf("offset").forGetter(foliagePlacer -> foliagePlacer.offset)
		);
	}

	public FoliagePlacer(IntProvider radius, IntProvider offset) {
		this.radius = radius;
		this.offset = offset;
	}

	protected abstract FoliagePlacerType<?> getType();

	public void generate(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.TreeNode treeNode,
		int j,
		int k
	) {
		this.generate(testableWorld, biConsumer, random, treeFeatureConfig, i, treeNode, j, k, this.getRandomOffset(random));
	}

	/**
	 * This is the main method used to generate foliage.
	 */
	protected abstract void generate(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.TreeNode treeNode,
		int radius,
		int j,
		int offset
	);

	public abstract int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config);

	public int getRandomRadius(Random random, int baseHeight) {
		return this.radius.get(random);
	}

	private int getRandomOffset(Random random) {
		return this.offset.get(random);
	}

	/**
	 * Used to exclude certain positions such as corners when creating a square of leaves.
	 */
	protected abstract boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk);

	/**
	 * Normalizes x and z coords before checking if they are invalid.
	 */
	protected boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		int i;
		int j;
		if (giantTrunk) {
			i = Math.min(Math.abs(dx), Math.abs(dx - 1));
			j = Math.min(Math.abs(dz), Math.abs(dz - 1));
		} else {
			i = Math.abs(dx);
			j = Math.abs(dz);
		}

		return this.isInvalidForLeaves(random, i, y, j, radius, giantTrunk);
	}

	/**
	 * Generates a square of leaves with the given radius. Sub-classes can use the method {@code isInvalidForLeaves} to exclude certain positions, such as corners.
	 */
	protected void generateSquare(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		BlockPos blockPos,
		int i,
		int y,
		boolean giantTrunk
	) {
		int j = giantTrunk ? 1 : 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = -i; k <= i + j; k++) {
			for (int l = -i; l <= i + j; l++) {
				if (!this.isPositionInvalid(random, k, y, l, i, giantTrunk)) {
					mutable.set(blockPos, k, y, l);
					placeFoliageBlock(testableWorld, biConsumer, random, treeFeatureConfig, mutable);
				}
			}
		}
	}

	protected static void placeFoliageBlock(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, TreeFeatureConfig config, BlockPos blockPos
	) {
		if (TreeFeature.canReplace(testableWorld, blockPos)) {
			biConsumer.accept(blockPos, config.foliageProvider.getBlockState(random, blockPos));
		}
	}

	/**
	 * A point on a tree to generate foliage around
	 */
	public static final class TreeNode {
		private final BlockPos center;
		private final int foliageRadius;
		private final boolean giantTrunk;

		public TreeNode(BlockPos center, int foliageRadius, boolean giantTrunk) {
			this.center = center;
			this.foliageRadius = foliageRadius;
			this.giantTrunk = giantTrunk;
		}

		public BlockPos getCenter() {
			return this.center;
		}

		public int getFoliageRadius() {
			return this.foliageRadius;
		}

		/**
		 * Whether this node is the top of a giant (2x2 block) trunk
		 */
		public boolean isGiantTrunk() {
			return this.giantTrunk;
		}
	}
}

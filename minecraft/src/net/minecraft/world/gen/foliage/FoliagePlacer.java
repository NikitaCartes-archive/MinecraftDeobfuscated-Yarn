package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer {
	public static final Codec<FoliagePlacer> TYPE_CODEC = Registry.FOLIAGE_PLACER_TYPE.getCodec().dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
	protected final IntProvider radius;
	protected final IntProvider offset;

	protected static <P extends FoliagePlacer> P2<Mu<P>, IntProvider, IntProvider> fillFoliagePlacerFields(Instance<P> instance) {
		return instance.group(
			IntProvider.createValidatingCodec(0, 16).fieldOf("radius").forGetter(placer -> placer.radius),
			IntProvider.createValidatingCodec(0, 16).fieldOf("offset").forGetter(placer -> placer.offset)
		);
	}

	public FoliagePlacer(IntProvider radius, IntProvider offset) {
		this.radius = radius;
		this.offset = offset;
	}

	protected abstract FoliagePlacerType<?> getType();

	public void generate(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius
	) {
		this.generate(world, replacer, random, config, trunkHeight, treeNode, foliageHeight, radius, this.getRandomOffset(random));
	}

	/**
	 * This is the main method used to generate foliage.
	 */
	protected abstract void generate(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		TreeFeatureConfig config,
		BlockPos centerPos,
		int radius,
		int y,
		boolean giantTrunk
	) {
		int i = giantTrunk ? 1 : 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = -radius; j <= radius + i; j++) {
			for (int k = -radius; k <= radius + i; k++) {
				if (!this.isPositionInvalid(random, j, y, k, radius, giantTrunk)) {
					mutable.set(centerPos, j, y, k);
					placeFoliageBlock(world, replacer, random, config, mutable);
				}
			}
		}
	}

	protected static void placeFoliageBlock(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos pos) {
		if (TreeFeature.canReplace(world, pos)) {
			BlockState blockState = config.foliageProvider.get(random, pos);
			if (blockState.contains(Properties.WATERLOGGED)) {
				blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(world.testFluidState(pos, fluidState -> fluidState.isEqualAndStill(Fluids.WATER))));
			}

			replacer.accept(pos, blockState);
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

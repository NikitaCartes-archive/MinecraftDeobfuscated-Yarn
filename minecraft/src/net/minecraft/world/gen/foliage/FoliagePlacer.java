package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer {
	public static final Codec<FoliagePlacer> TYPE_CODEC = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
	protected final UniformIntDistribution radius;
	protected final UniformIntDistribution offset;

	protected static <P extends FoliagePlacer> P2<Mu<P>, UniformIntDistribution, UniformIntDistribution> fillFoliagePlacerFields(Instance<P> instance) {
		return instance.group(
			UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("radius").forGetter(foliagePlacer -> foliagePlacer.radius),
			UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("offset").forGetter(foliagePlacer -> foliagePlacer.offset)
		);
	}

	public FoliagePlacer(UniformIntDistribution radius, UniformIntDistribution offset) {
		this.radius = radius;
		this.offset = offset;
	}

	protected abstract FoliagePlacerType<?> getType();

	public void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		BlockBox box
	) {
		this.generate(world, random, config, trunkHeight, treeNode, foliageHeight, radius, leaves, this.getRandomOffset(random), box);
	}

	/**
	 * This is the main method used to generate foliage.
	 */
	protected abstract void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int offset,
		BlockBox box
	);

	public abstract int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config);

	public int getRandomRadius(Random random, int baseHeight) {
		return this.radius.getValue(random);
	}

	private int getRandomOffset(Random random) {
		return this.offset.getValue(random);
	}

	/**
	 * Used to exclude certain positions such as corners when creating a square of leaves.
	 */
	protected abstract boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int y, int dz, boolean giantTrunk);

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
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		BlockPos pos,
		int radius,
		Set<BlockPos> leaves,
		int y,
		boolean giantTrunk,
		BlockBox box
	) {
		int i = giantTrunk ? 1 : 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = -radius; j <= radius + i; j++) {
			for (int k = -radius; k <= radius + i; k++) {
				if (!this.isPositionInvalid(random, j, y, k, radius, giantTrunk)) {
					mutable.set(pos, j, y, k);
					if (TreeFeature.canReplace(world, mutable)) {
						world.setBlockState(mutable, config.leavesProvider.getBlockState(random, mutable), 19);
						box.encompass(new BlockBox(mutable, mutable));
						leaves.add(mutable.toImmutable());
					}
				}
			}
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

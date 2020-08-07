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
	public static final Codec<FoliagePlacer> CODEC = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::getType, FoliagePlacerType::getCodec);
	protected final UniformIntDistribution radius;
	protected final UniformIntDistribution offset;

	protected static <P extends FoliagePlacer> P2<Mu<P>, UniformIntDistribution, UniformIntDistribution> method_30411(Instance<P> instance) {
		return instance.group(
			UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("radius").forGetter(foliagePlacer -> foliagePlacer.radius),
			UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("offset").forGetter(foliagePlacer -> foliagePlacer.offset)
		);
	}

	public FoliagePlacer(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2) {
		this.radius = uniformIntDistribution;
		this.offset = uniformIntDistribution2;
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
		BlockBox blockBox
	) {
		this.generate(world, random, config, trunkHeight, treeNode, foliageHeight, radius, leaves, this.method_27386(random), blockBox);
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
		int i,
		BlockBox blockBox
	);

	public abstract int getHeight(Random random, int trunkHeight, TreeFeatureConfig config);

	public int getRadius(Random random, int baseHeight) {
		return this.radius.getValue(random);
	}

	private int method_27386(Random random) {
		return this.offset.getValue(random);
	}

	protected abstract boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl);

	protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
		int m;
		int n;
		if (bl) {
			m = Math.min(Math.abs(i), Math.abs(i - 1));
			n = Math.min(Math.abs(k), Math.abs(k - 1));
		} else {
			m = Math.abs(i);
			n = Math.abs(k);
		}

		return this.isInvalidForLeaves(random, m, j, n, l, bl);
	}

	protected void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		BlockPos blockPos,
		int baseHeight,
		Set<BlockPos> set,
		int i,
		boolean giantTrunk,
		BlockBox blockBox
	) {
		int j = giantTrunk ? 1 : 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = -baseHeight; k <= baseHeight + j; k++) {
			for (int l = -baseHeight; l <= baseHeight + j; l++) {
				if (!this.method_27387(random, k, i, l, baseHeight, giantTrunk)) {
					mutable.set(blockPos, k, i, l);
					if (TreeFeature.canReplace(world, mutable)) {
						world.setBlockState(mutable, config.leavesProvider.getBlockState(random, mutable), 19);
						blockBox.encompass(new BlockBox(mutable, mutable));
						set.add(mutable.toImmutable());
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

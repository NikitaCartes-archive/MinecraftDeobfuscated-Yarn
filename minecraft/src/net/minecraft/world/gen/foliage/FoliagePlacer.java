package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public abstract class FoliagePlacer {
	public static final Codec<FoliagePlacer> field_24931 = Registry.FOLIAGE_PLACER_TYPE.dispatch(FoliagePlacer::method_28843, FoliagePlacerType::method_28849);
	protected final int radius;
	protected final int randomRadius;
	protected final int offset;
	protected final int randomOffset;

	protected static <P extends FoliagePlacer> P4<Mu<P>, Integer, Integer, Integer, Integer> method_28846(Instance<P> instance) {
		return instance.group(
			Codec.INT.fieldOf("radius").forGetter(foliagePlacer -> foliagePlacer.radius),
			Codec.INT.fieldOf("radius_random").forGetter(foliagePlacer -> foliagePlacer.randomRadius),
			Codec.INT.fieldOf("offset").forGetter(foliagePlacer -> foliagePlacer.offset),
			Codec.INT.fieldOf("offset_random").forGetter(foliagePlacer -> foliagePlacer.randomOffset)
		);
	}

	public FoliagePlacer(int radius, int randomRadius, int offset, int randomOffset) {
		this.radius = radius;
		this.randomRadius = randomRadius;
		this.offset = offset;
		this.randomOffset = randomOffset;
	}

	protected abstract FoliagePlacerType<?> method_28843();

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
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	private int method_27386(Random random) {
		return this.offset + random.nextInt(this.randomOffset + 1);
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

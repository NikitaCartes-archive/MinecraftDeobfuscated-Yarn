package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class LargeOakTrunkPlacer extends TrunkPlacer {
	public static final MapCodec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
		instance -> fillTrunkPlacerFields(instance).apply(instance, LargeOakTrunkPlacer::new)
	);
	private static final double field_31524 = 0.618;
	private static final double field_31525 = 1.382;
	private static final double field_31526 = 0.381;
	private static final double field_31527 = 0.328;

	public LargeOakTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TrunkPlacerType.FANCY_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config
	) {
		int i = 5;
		int j = height + 2;
		int k = MathHelper.floor((double)j * 0.618);
		setToDirt(world, replacer, random, startPos.down(), config);
		double d = 1.0;
		int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
		int m = startPos.getY() + k;
		int n = j - 5;
		List<LargeOakTrunkPlacer.BranchPosition> list = Lists.<LargeOakTrunkPlacer.BranchPosition>newArrayList();
		list.add(new LargeOakTrunkPlacer.BranchPosition(startPos.up(n), m));

		for (; n >= 0; n--) {
			float f = shouldGenerateBranch(j, n);
			if (!(f < 0.0F)) {
				for (int o = 0; o < l; o++) {
					double e = 1.0;
					double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
					double h = (double)(random.nextFloat() * 2.0F) * Math.PI;
					double p = g * Math.sin(h) + 0.5;
					double q = g * Math.cos(h) + 0.5;
					BlockPos blockPos = startPos.add(MathHelper.floor(p), n - 1, MathHelper.floor(q));
					BlockPos blockPos2 = blockPos.up(5);
					if (this.makeOrCheckBranch(world, replacer, random, blockPos, blockPos2, false, config)) {
						int r = startPos.getX() - blockPos.getX();
						int s = startPos.getZ() - blockPos.getZ();
						double t = (double)blockPos.getY() - Math.sqrt((double)(r * r + s * s)) * 0.381;
						int u = t > (double)m ? m : (int)t;
						BlockPos blockPos3 = new BlockPos(startPos.getX(), u, startPos.getZ());
						if (this.makeOrCheckBranch(world, replacer, random, blockPos3, blockPos, false, config)) {
							list.add(new LargeOakTrunkPlacer.BranchPosition(blockPos, blockPos3.getY()));
						}
					}
				}
			}
		}

		this.makeOrCheckBranch(world, replacer, random, startPos, startPos.up(k), true, config);
		this.makeBranches(world, replacer, random, j, startPos, list, config);
		List<FoliagePlacer.TreeNode> list2 = Lists.<FoliagePlacer.TreeNode>newArrayList();

		for (LargeOakTrunkPlacer.BranchPosition branchPosition : list) {
			if (this.isHighEnough(j, branchPosition.getEndY() - startPos.getY())) {
				list2.add(branchPosition.node);
			}
		}

		return list2;
	}

	private boolean makeOrCheckBranch(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, BlockPos branchPos, boolean make, TreeFeatureConfig config
	) {
		if (!make && Objects.equals(startPos, branchPos)) {
			return true;
		} else {
			BlockPos blockPos = branchPos.add(-startPos.getX(), -startPos.getY(), -startPos.getZ());
			int i = this.getLongestSide(blockPos);
			float f = (float)blockPos.getX() / (float)i;
			float g = (float)blockPos.getY() / (float)i;
			float h = (float)blockPos.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos2 = startPos.add(MathHelper.floor(0.5F + (float)j * f), MathHelper.floor(0.5F + (float)j * g), MathHelper.floor(0.5F + (float)j * h));
				if (make) {
					this.getAndSetState(world, replacer, random, blockPos2, config, state -> state.withIfExists(PillarBlock.AXIS, this.getLogAxis(startPos, blockPos2)));
				} else if (!this.canReplaceOrIsLog(world, blockPos2)) {
					return false;
				}
			}

			return true;
		}
	}

	private int getLongestSide(BlockPos offset) {
		int i = MathHelper.abs(offset.getX());
		int j = MathHelper.abs(offset.getY());
		int k = MathHelper.abs(offset.getZ());
		return Math.max(i, Math.max(j, k));
	}

	private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(branchEnd.getX() - branchStart.getX());
		int j = Math.abs(branchEnd.getZ() - branchStart.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.X;
			} else {
				axis = Direction.Axis.Z;
			}
		}

		return axis;
	}

	private boolean isHighEnough(int treeHeight, int height) {
		return (double)height >= (double)treeHeight * 0.2;
	}

	private void makeBranches(
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		int treeHeight,
		BlockPos startPos,
		List<LargeOakTrunkPlacer.BranchPosition> branchPositions,
		TreeFeatureConfig config
	) {
		for (LargeOakTrunkPlacer.BranchPosition branchPosition : branchPositions) {
			int i = branchPosition.getEndY();
			BlockPos blockPos = new BlockPos(startPos.getX(), i, startPos.getZ());
			if (!blockPos.equals(branchPosition.node.getCenter()) && this.isHighEnough(treeHeight, i - startPos.getY())) {
				this.makeOrCheckBranch(world, replacer, random, blockPos, branchPosition.node.getCenter(), true, config);
			}
		}
	}

	/**
	 * If the returned value is greater than or equal to 0, a branch will be generated.
	 */
	private static float shouldGenerateBranch(int treeHeight, int height) {
		if ((float)height < (float)treeHeight * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)treeHeight / 2.0F;
			float g = f - (float)height;
			float h = MathHelper.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	static class BranchPosition {
		final FoliagePlacer.TreeNode node;
		private final int endY;

		public BranchPosition(BlockPos pos, int width) {
			this.node = new FoliagePlacer.TreeNode(pos, 0, false);
			this.endY = width;
		}

		public int getEndY() {
			return this.endY;
		}
	}
}

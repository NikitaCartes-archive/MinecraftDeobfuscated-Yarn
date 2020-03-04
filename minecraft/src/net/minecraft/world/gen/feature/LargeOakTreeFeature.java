package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class LargeOakTreeFeature extends AbstractTreeFeature<BranchedTreeFeatureConfig> {
	public LargeOakTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
		super(function);
	}

	private void makeLeafLayer(
		ModifiableTestableWorld world, Random random, BlockPos pos, float f, Set<BlockPos> leaves, BlockBox box, BranchedTreeFeatureConfig config
	) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					this.setLeavesBlockState(world, random, pos.add(j, 0, k), leaves, box, config);
				}
			}
		}
	}

	private float getBaseBranchSize(int treeHeight, int branchCount) {
		if ((float)branchCount < (float)treeHeight * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)treeHeight / 2.0F;
			float g = f - (float)branchCount;
			float h = MathHelper.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	private float getLeafRadiusForLayer(int i) {
		if (i < 0 || i >= 5) {
			return -1.0F;
		} else {
			return i != 0 && i != 4 ? 3.0F : 2.0F;
		}
	}

	private void makeLeaves(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> leaves, BlockBox box, BranchedTreeFeatureConfig config) {
		for (int i = 0; i < 5; i++) {
			this.makeLeafLayer(world, random, pos.up(i), this.getLeafRadiusForLayer(i), leaves, box, config);
		}
	}

	private int makeOrCheckBranch(
		ModifiableTestableWorld world,
		Random random,
		BlockPos start,
		BlockPos end,
		boolean make,
		Set<BlockPos> logs,
		BlockBox blockBox,
		BranchedTreeFeatureConfig config
	) {
		if (!make && Objects.equals(start, end)) {
			return -1;
		} else {
			BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
			int i = this.getLongestSide(blockPos);
			float f = (float)blockPos.getX() / (float)i;
			float g = (float)blockPos.getY() / (float)i;
			float h = (float)blockPos.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos2 = start.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (make) {
					this.setBlockState(
						world, blockPos2, config.trunkProvider.getBlockState(random, blockPos2).with(LogBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox
					);
					logs.add(blockPos2);
				} else if (!canTreeReplace(world, blockPos2)) {
					return j;
				}
			}

			return -1;
		}
	}

	private int getLongestSide(BlockPos box) {
		int i = MathHelper.abs(box.getX());
		int j = MathHelper.abs(box.getY());
		int k = MathHelper.abs(box.getZ());
		if (k > i && k > j) {
			return k;
		} else {
			return j > i ? j : i;
		}
	}

	private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(branchEnd.getX() - branchStart.getX());
		int j = Math.abs(branchEnd.getZ() - branchStart.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.X;
			} else if (j == k) {
				axis = Direction.Axis.Z;
			}
		}

		return axis;
	}

	private void makeLeaves(
		ModifiableTestableWorld world,
		Random random,
		int i,
		BlockPos pos,
		List<LargeOakTreeFeature.BranchPos> list,
		Set<BlockPos> set,
		BlockBox box,
		BranchedTreeFeatureConfig config
	) {
		for (LargeOakTreeFeature.BranchPos branchPos : list) {
			if (this.isHighEnough(i, branchPos.getEndY() - pos.getY())) {
				this.makeLeaves(world, random, branchPos, set, box, config);
			}
		}
	}

	private boolean isHighEnough(int treeHeight, int height) {
		return (double)height >= (double)treeHeight * 0.2;
	}

	private void makeTrunk(
		ModifiableTestableWorld world, Random random, BlockPos pos, int height, Set<BlockPos> set, BlockBox box, BranchedTreeFeatureConfig config
	) {
		this.makeOrCheckBranch(world, random, pos, pos.up(height), true, set, box, config);
	}

	private void makeBranches(
		ModifiableTestableWorld world,
		Random random,
		int treeHeight,
		BlockPos treePosition,
		List<LargeOakTreeFeature.BranchPos> branchPositions,
		Set<BlockPos> set,
		BlockBox box,
		BranchedTreeFeatureConfig config
	) {
		for (LargeOakTreeFeature.BranchPos branchPos : branchPositions) {
			int i = branchPos.getEndY();
			BlockPos blockPos = new BlockPos(treePosition.getX(), i, treePosition.getZ());
			if (!blockPos.equals(branchPos) && this.isHighEnough(treeHeight, i - treePosition.getY())) {
				this.makeOrCheckBranch(world, random, blockPos, branchPos, true, set, box, config);
			}
		}
	}

	public boolean generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig
	) {
		Random random2 = new Random(random.nextLong());
		int i = this.getTreeHeight(modifiableTestableWorld, random, blockPos, 5 + random2.nextInt(12), set, blockBox, branchedTreeFeatureConfig);
		if (i == -1) {
			return false;
		} else {
			this.setToDirt(modifiableTestableWorld, blockPos.down());
			int j = (int)((double)i * 0.618);
			if (j >= i) {
				j = i - 1;
			}

			double d = 1.0;
			int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
			if (k < 1) {
				k = 1;
			}

			int l = blockPos.getY() + j;
			int m = i - 5;
			List<LargeOakTreeFeature.BranchPos> list = Lists.<LargeOakTreeFeature.BranchPos>newArrayList();
			list.add(new LargeOakTreeFeature.BranchPos(blockPos.up(m), l));

			for (; m >= 0; m--) {
				float f = this.getBaseBranchSize(i, m);
				if (!(f < 0.0F)) {
					for (int n = 0; n < k; n++) {
						double e = 1.0;
						double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
						double h = (double)(random2.nextFloat() * 2.0F) * Math.PI;
						double o = g * Math.sin(h) + 0.5;
						double p = g * Math.cos(h) + 0.5;
						BlockPos blockPos2 = blockPos.add(o, (double)(m - 1), p);
						BlockPos blockPos3 = blockPos2.up(5);
						if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos2, blockPos3, false, set, blockBox, branchedTreeFeatureConfig) == -1) {
							int q = blockPos.getX() - blockPos2.getX();
							int r = blockPos.getZ() - blockPos2.getZ();
							double s = (double)blockPos2.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
							if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos4, blockPos2, false, set, blockBox, branchedTreeFeatureConfig) == -1) {
								list.add(new LargeOakTreeFeature.BranchPos(blockPos2, blockPos4.getY()));
							}
						}
					}
				}
			}

			this.makeLeaves(modifiableTestableWorld, random, i, blockPos, list, set2, blockBox, branchedTreeFeatureConfig);
			this.makeTrunk(modifiableTestableWorld, random, blockPos, j, set, blockBox, branchedTreeFeatureConfig);
			this.makeBranches(modifiableTestableWorld, random, i, blockPos, list, set, blockBox, branchedTreeFeatureConfig);
			return true;
		}
	}

	private int getTreeHeight(
		ModifiableTestableWorld world, Random random, BlockPos pos, int height, Set<BlockPos> logs, BlockBox box, BranchedTreeFeatureConfig config
	) {
		if (!isDirtOrGrass(world, pos.down())) {
			return -1;
		} else {
			int i = this.makeOrCheckBranch(world, random, pos, pos.up(height - 1), false, logs, box, config);
			if (i == -1) {
				return height;
			} else {
				return i < 6 ? -1 : i;
			}
		}
	}

	static class BranchPos extends BlockPos {
		private final int endY;

		public BranchPos(BlockPos pos, int endY) {
			super(pos.getX(), pos.getY(), pos.getZ());
			this.endY = endY;
		}

		public int getEndY() {
			return this.endY;
		}
	}
}

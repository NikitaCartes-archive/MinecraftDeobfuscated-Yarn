package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class LargeOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();

	public LargeOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
		super(configFactory, emitNeighborBlockUpdates);
	}

	private void makeLeafLayer(ModifiableTestableWorld modifiableTestableWorld, BlockPos pos, float radius, BlockBox blockBox, Set<BlockPos> set) {
		int i = (int)((double)radius + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(radius * radius)) {
					BlockPos blockPos = pos.add(j, 0, k);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
					}
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

	private void makeLeaves(ModifiableTestableWorld world, BlockPos pos, BlockBox blockBox, Set<BlockPos> set) {
		for (int i = 0; i < 5; i++) {
			this.makeLeafLayer(world, pos.up(i), this.getLeafRadiusForLayer(i), blockBox, set);
		}
	}

	private int makeOrCheckBranch(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos start, BlockPos end, boolean make, BlockBox blockBox) {
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
					this.setBlockState(logPositions, world, blockPos2, LOG.with(LogBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox);
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
		int treeHeight,
		BlockPos treePos,
		List<LargeOakTreeFeature.BranchPosition> branchPositions,
		BlockBox blockBox,
		Set<BlockPos> set
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : branchPositions) {
			if (this.isHighEnough(treeHeight, branchPosition.getEndY() - treePos.getY())) {
				this.makeLeaves(world, branchPosition, blockBox, set);
			}
		}
	}

	private boolean isHighEnough(int treeHeight, int height) {
		return (double)height >= (double)treeHeight * 0.2;
	}

	private void makeTrunk(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos pos, int height, BlockBox blockBox) {
		this.makeOrCheckBranch(logPositions, world, pos, pos.up(height), true, blockBox);
	}

	private void makeBranches(
		Set<BlockPos> logPositions,
		ModifiableTestableWorld world,
		int treeHeight,
		BlockPos treePosition,
		List<LargeOakTreeFeature.BranchPosition> branchPositions,
		BlockBox blockBox
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : branchPositions) {
			int i = branchPosition.getEndY();
			BlockPos blockPos = new BlockPos(treePosition.getX(), i, treePosition.getZ());
			if (!blockPos.equals(branchPosition) && this.isHighEnough(treeHeight, i - treePosition.getY())) {
				this.makeOrCheckBranch(logPositions, world, blockPos, branchPosition, true, blockBox);
			}
		}
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		Random random2 = new Random(random.nextLong());
		int i = this.getTreeHeight(logPositions, world, pos, 5 + random2.nextInt(12), blockBox);
		if (i == -1) {
			return false;
		} else {
			this.setToDirt(world, pos.down());
			int j = (int)((double)i * 0.618);
			if (j >= i) {
				j = i - 1;
			}

			double d = 1.0;
			int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
			if (k < 1) {
				k = 1;
			}

			int l = pos.getY() + j;
			int m = i - 5;
			List<LargeOakTreeFeature.BranchPosition> list = Lists.<LargeOakTreeFeature.BranchPosition>newArrayList();
			list.add(new LargeOakTreeFeature.BranchPosition(pos.up(m), l));

			for (; m >= 0; m--) {
				float f = this.getBaseBranchSize(i, m);
				if (!(f < 0.0F)) {
					for (int n = 0; n < k; n++) {
						double e = 1.0;
						double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
						double h = (double)(random2.nextFloat() * 2.0F) * Math.PI;
						double o = g * Math.sin(h) + 0.5;
						double p = g * Math.cos(h) + 0.5;
						BlockPos blockPos = pos.add(o, (double)(m - 1), p);
						BlockPos blockPos2 = blockPos.up(5);
						if (this.makeOrCheckBranch(logPositions, world, blockPos, blockPos2, false, blockBox) == -1) {
							int q = pos.getX() - blockPos.getX();
							int r = pos.getZ() - blockPos.getZ();
							double s = (double)blockPos.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos3 = new BlockPos(pos.getX(), t, pos.getZ());
							if (this.makeOrCheckBranch(logPositions, world, blockPos3, blockPos, false, blockBox) == -1) {
								list.add(new LargeOakTreeFeature.BranchPosition(blockPos, blockPos3.getY()));
							}
						}
					}
				}
			}

			this.makeLeaves(world, i, pos, list, blockBox, logPositions);
			this.makeTrunk(logPositions, world, pos, j, blockBox);
			this.makeBranches(logPositions, world, i, pos, list, blockBox);
			return true;
		}
	}

	private int getTreeHeight(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos pos, int height, BlockBox blockBox) {
		if (!isDirtOrGrass(world, pos.down())) {
			return -1;
		} else {
			int i = this.makeOrCheckBranch(logPositions, world, pos, pos.up(height - 1), false, blockBox);
			if (i == -1) {
				return height;
			} else {
				return i < 6 ? -1 : i;
			}
		}
	}

	static class BranchPosition extends BlockPos {
		private final int endY;

		public BranchPosition(BlockPos pos, int endY) {
			super(pos.getX(), pos.getY(), pos.getZ());
			this.endY = endY;
		}

		public int getEndY() {
			return this.endY;
		}
	}
}

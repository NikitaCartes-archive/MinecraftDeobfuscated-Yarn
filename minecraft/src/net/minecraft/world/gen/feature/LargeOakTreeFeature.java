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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;

public class LargeOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10503.getDefaultState();

	public LargeOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	private void makeLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, float f, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set
	) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
					}
				}
			}
		}
	}

	private float getBaseBranchSize(int i, int j) {
		if ((float)j < (float)i * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)i / 2.0F;
			float g = f - (float)j;
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

	private void makeLeaves(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox, Set<BlockPos> set) {
		for (int i = 0; i < 5; i++) {
			this.makeLeafLayer(modifiableTestableWorld, blockPos.up(i), this.getLeafRadiusForLayer(i), mutableIntBoundingBox, set);
		}
	}

	private int makeOrCheckBranch(
		Set<BlockPos> set,
		ModifiableTestableWorld modifiableTestableWorld,
		BlockPos blockPos,
		BlockPos blockPos2,
		boolean bl,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
		if (!bl && Objects.equals(blockPos, blockPos2)) {
			return -1;
		} else {
			BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
			int i = this.getLongestSide(blockPos3);
			float f = (float)blockPos3.getX() / (float)i;
			float g = (float)blockPos3.getY() / (float)i;
			float h = (float)blockPos3.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos4 = blockPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (bl) {
					this.setBlockState(set, modifiableTestableWorld, blockPos4, LOG.with(LogBlock.AXIS, this.getLogAxis(blockPos, blockPos4)), mutableIntBoundingBox);
				} else if (!canTreeReplace(modifiableTestableWorld, blockPos4)) {
					return j;
				}
			}

			return -1;
		}
	}

	private int getLongestSide(BlockPos blockPos) {
		int i = MathHelper.abs(blockPos.getX());
		int j = MathHelper.abs(blockPos.getY());
		int k = MathHelper.abs(blockPos.getZ());
		if (k > i && k > j) {
			return k;
		} else {
			return j > i ? j : i;
		}
	}

	private Direction.Axis getLogAxis(BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = Direction.Axis.field_11052;
		int i = Math.abs(blockPos2.getX() - blockPos.getX());
		int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.field_11048;
			} else if (j == k) {
				axis = Direction.Axis.field_11051;
			}
		}

		return axis;
	}

	private void makeLeaves(
		ModifiableTestableWorld modifiableTestableWorld,
		int i,
		BlockPos blockPos,
		List<LargeOakTreeFeature.BranchPosition> list,
		MutableIntBoundingBox mutableIntBoundingBox,
		Set<BlockPos> set
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : list) {
			if (this.isHighEnough(i, branchPosition.getEndY() - blockPos.getY())) {
				this.makeLeaves(modifiableTestableWorld, branchPosition, mutableIntBoundingBox, set);
			}
		}
	}

	private boolean isHighEnough(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void makeTrunk(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, MutableIntBoundingBox mutableIntBoundingBox
	) {
		this.makeOrCheckBranch(set, modifiableTestableWorld, blockPos, blockPos.up(i), true, mutableIntBoundingBox);
	}

	private void makeBranches(
		Set<BlockPos> set,
		ModifiableTestableWorld modifiableTestableWorld,
		int i,
		BlockPos blockPos,
		List<LargeOakTreeFeature.BranchPosition> list,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : list) {
			int j = branchPosition.getEndY();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
			if (!blockPos2.equals(branchPosition) && this.isHighEnough(i, j - blockPos.getY())) {
				this.makeOrCheckBranch(set, modifiableTestableWorld, blockPos2, branchPosition, true, mutableIntBoundingBox);
			}
		}
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		Random random2 = new Random(random.nextLong());
		int i = this.getTreeHeight(set, modifiableTestableWorld, blockPos, 5 + random2.nextInt(12), mutableIntBoundingBox);
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
			List<LargeOakTreeFeature.BranchPosition> list = Lists.<LargeOakTreeFeature.BranchPosition>newArrayList();
			list.add(new LargeOakTreeFeature.BranchPosition(blockPos.up(m), l));

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
						if (this.makeOrCheckBranch(set, modifiableTestableWorld, blockPos2, blockPos3, false, mutableIntBoundingBox) == -1) {
							int q = blockPos.getX() - blockPos2.getX();
							int r = blockPos.getZ() - blockPos2.getZ();
							double s = (double)blockPos2.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
							if (this.makeOrCheckBranch(set, modifiableTestableWorld, blockPos4, blockPos2, false, mutableIntBoundingBox) == -1) {
								list.add(new LargeOakTreeFeature.BranchPosition(blockPos2, blockPos4.getY()));
							}
						}
					}
				}
			}

			this.makeLeaves(modifiableTestableWorld, i, blockPos, list, mutableIntBoundingBox, set);
			this.makeTrunk(set, modifiableTestableWorld, blockPos, j, mutableIntBoundingBox);
			this.makeBranches(set, modifiableTestableWorld, i, blockPos, list, mutableIntBoundingBox);
			return true;
		}
	}

	private int getTreeHeight(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, MutableIntBoundingBox mutableIntBoundingBox
	) {
		if (!isDirtOrGrass(modifiableTestableWorld, blockPos.down())) {
			return -1;
		} else {
			int j = this.makeOrCheckBranch(set, modifiableTestableWorld, blockPos, blockPos.up(i - 1), false, mutableIntBoundingBox);
			if (j == -1) {
				return i;
			} else {
				return j < 6 ? -1 : j;
			}
		}
	}

	static class BranchPosition extends BlockPos {
		private final int endY;

		public BranchPosition(BlockPos blockPos, int i) {
			super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.endY = i;
		}

		public int getEndY() {
			return this.endY;
		}
	}
}

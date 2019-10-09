package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.class_4640;
import net.minecraft.block.LogBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;

public class LargeOakTreeFeature extends AbstractTreeFeature<class_4640> {
	public LargeOakTreeFeature(Function<Dynamic<?>, ? extends class_4640> function) {
		super(function);
	}

	private void makeLeafLayer(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, float f, Set<BlockPos> set, BlockBox blockBox, class_4640 arg
	) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					this.method_23383(modifiableTestableWorld, random, blockPos.add(j, 0, k), set, blockBox, arg);
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

	private void makeLeaves(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, BlockBox blockBox, class_4640 arg
	) {
		for (int i = 0; i < 5; i++) {
			this.makeLeafLayer(modifiableTestableWorld, random, blockPos.up(i), this.getLeafRadiusForLayer(i), set, blockBox, arg);
		}
	}

	private int makeOrCheckBranch(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		BlockPos blockPos2,
		boolean bl,
		Set<BlockPos> set,
		BlockBox blockBox,
		class_4640 arg
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
					this.setBlockState(
						modifiableTestableWorld, blockPos4, arg.field_21288.method_23455(random, blockPos4).with(LogBlock.AXIS, this.getLogAxis(blockPos, blockPos4)), blockBox
					);
					set.add(blockPos4);
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
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(blockPos2.getX() - blockPos.getX());
		int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
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
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		int i,
		BlockPos blockPos,
		List<LargeOakTreeFeature.BranchPosition> list,
		Set<BlockPos> set,
		BlockBox blockBox,
		class_4640 arg
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : list) {
			if (this.isHighEnough(i, branchPosition.getEndY() - blockPos.getY())) {
				this.makeLeaves(modifiableTestableWorld, random, branchPosition, set, blockBox, arg);
			}
		}
	}

	private boolean isHighEnough(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void makeTrunk(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, class_4640 arg
	) {
		this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos, blockPos.up(i), true, set, blockBox, arg);
	}

	private void makeBranches(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		int i,
		BlockPos blockPos,
		List<LargeOakTreeFeature.BranchPosition> list,
		Set<BlockPos> set,
		BlockBox blockBox,
		class_4640 arg
	) {
		for (LargeOakTreeFeature.BranchPosition branchPosition : list) {
			int j = branchPosition.getEndY();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
			if (!blockPos2.equals(branchPosition) && this.isHighEnough(i, j - blockPos.getY())) {
				this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos2, branchPosition, true, set, blockBox, arg);
			}
		}
	}

	public boolean method_23394(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, class_4640 arg
	) {
		Random random2 = new Random(random.nextLong());
		int i = this.getTreeHeight(modifiableTestableWorld, random, blockPos, 5 + random2.nextInt(12), set, blockBox, arg);
		if (i == -1) {
			return false;
		} else {
			this.setToDirt(modifiableTestableWorld, blockPos.method_10074());
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
						if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos2, blockPos3, false, set, blockBox, arg) == -1) {
							int q = blockPos.getX() - blockPos2.getX();
							int r = blockPos.getZ() - blockPos2.getZ();
							double s = (double)blockPos2.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
							if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos4, blockPos2, false, set, blockBox, arg) == -1) {
								list.add(new LargeOakTreeFeature.BranchPosition(blockPos2, blockPos4.getY()));
							}
						}
					}
				}
			}

			this.makeLeaves(modifiableTestableWorld, random, i, blockPos, list, set2, blockBox, arg);
			this.makeTrunk(modifiableTestableWorld, random, blockPos, j, set, blockBox, arg);
			this.makeBranches(modifiableTestableWorld, random, i, blockPos, list, set, blockBox, arg);
			return true;
		}
	}

	private int getTreeHeight(
		ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, int i, Set<BlockPos> set, BlockBox blockBox, class_4640 arg
	) {
		if (!isDirtOrGrass(modifiableTestableWorld, blockPos.method_10074())) {
			return -1;
		} else {
			int j = this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos, blockPos.up(i - 1), false, set, blockBox, arg);
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

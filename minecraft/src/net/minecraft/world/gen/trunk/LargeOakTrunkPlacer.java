package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class LargeOakTrunkPlacer extends TrunkPlacer {
	public static final Codec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> method_28904(instance).apply(instance, LargeOakTrunkPlacer::new));

	public LargeOakTrunkPlacer(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected TrunkPlacerType<?> method_28903() {
		return TrunkPlacerType.FANCY_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(
		ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig
	) {
		int i = 5;
		int j = trunkHeight + 2;
		int k = MathHelper.floor((double)j * 0.618);
		method_27400(world, pos.down());
		double d = 1.0;
		int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
		int m = pos.getY() + k;
		int n = j - 5;
		List<LargeOakTrunkPlacer.BranchPosition> list = Lists.<LargeOakTrunkPlacer.BranchPosition>newArrayList();
		list.add(new LargeOakTrunkPlacer.BranchPosition(pos.up(n), m));

		for (; n >= 0; n--) {
			float f = this.method_27396(j, n);
			if (!(f < 0.0F)) {
				for (int o = 0; o < l; o++) {
					double e = 1.0;
					double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
					double h = (double)(random.nextFloat() * 2.0F) * Math.PI;
					double p = g * Math.sin(h) + 0.5;
					double q = g * Math.cos(h) + 0.5;
					BlockPos blockPos = pos.add(p, (double)(n - 1), q);
					BlockPos blockPos2 = blockPos.up(5);
					if (this.makeOrCheckBranch(world, random, blockPos, blockPos2, false, set, blockBox, treeFeatureConfig)) {
						int r = pos.getX() - blockPos.getX();
						int s = pos.getZ() - blockPos.getZ();
						double t = (double)blockPos.getY() - Math.sqrt((double)(r * r + s * s)) * 0.381;
						int u = t > (double)m ? m : (int)t;
						BlockPos blockPos3 = new BlockPos(pos.getX(), u, pos.getZ());
						if (this.makeOrCheckBranch(world, random, blockPos3, blockPos, false, set, blockBox, treeFeatureConfig)) {
							list.add(new LargeOakTrunkPlacer.BranchPosition(blockPos, blockPos3.getY()));
						}
					}
				}
			}
		}

		this.makeOrCheckBranch(world, random, pos, pos.up(k), true, set, blockBox, treeFeatureConfig);
		this.makeBranches(world, random, j, pos, list, set, blockBox, treeFeatureConfig);
		List<FoliagePlacer.TreeNode> list2 = Lists.<FoliagePlacer.TreeNode>newArrayList();

		for (LargeOakTrunkPlacer.BranchPosition branchPosition : list) {
			if (this.isHighEnough(j, branchPosition.getEndY() - pos.getY())) {
				list2.add(branchPosition.node);
			}
		}

		return list2;
	}

	private boolean makeOrCheckBranch(
		ModifiableTestableWorld world, Random random, BlockPos start, BlockPos end, boolean make, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig config
	) {
		if (!make && Objects.equals(start, end)) {
			return true;
		} else {
			BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
			int i = this.getLongestSide(blockPos);
			float f = (float)blockPos.getX() / (float)i;
			float g = (float)blockPos.getY() / (float)i;
			float h = (float)blockPos.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos2 = start.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (make) {
					method_27404(world, blockPos2, config.trunkProvider.getBlockState(random, blockPos2).with(PillarBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox);
					set.add(blockPos2.toImmutable());
				} else if (!TreeFeature.canTreeReplace(world, blockPos2)) {
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
		ModifiableTestableWorld world,
		Random random,
		int treeHeight,
		BlockPos treePos,
		List<LargeOakTrunkPlacer.BranchPosition> branches,
		Set<BlockPos> set,
		BlockBox blockBox,
		TreeFeatureConfig config
	) {
		for (LargeOakTrunkPlacer.BranchPosition branchPosition : branches) {
			int i = branchPosition.getEndY();
			BlockPos blockPos = new BlockPos(treePos.getX(), i, treePos.getZ());
			if (!blockPos.equals(branchPosition.node.getCenter()) && this.isHighEnough(treeHeight, i - treePos.getY())) {
				this.makeOrCheckBranch(world, random, blockPos, branchPosition.node.getCenter(), true, set, blockBox, config);
			}
		}
	}

	private float method_27396(int i, int j) {
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

	static class BranchPosition {
		private final FoliagePlacer.TreeNode node;
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

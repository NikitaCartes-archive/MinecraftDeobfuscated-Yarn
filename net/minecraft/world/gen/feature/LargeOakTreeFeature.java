/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class LargeOakTreeFeature
extends AbstractTreeFeature<BranchedTreeFeatureConfig> {
    public LargeOakTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
        super(function);
    }

    private void makeLeafLayer(ModifiableTestableWorld world, Random random, BlockPos pos, float f, Set<BlockPos> leaves, BlockBox box, BranchedTreeFeatureConfig config) {
        int i = (int)((double)f + 0.618);
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                if (!(Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f))) continue;
                this.setLeavesBlockState(world, random, pos.add(j, 0, k), leaves, box, config);
            }
        }
    }

    private float getBaseBranchSize(int treeHeight, int branchCount) {
        if ((float)branchCount < (float)treeHeight * 0.3f) {
            return -1.0f;
        }
        float f = (float)treeHeight / 2.0f;
        float g = f - (float)branchCount;
        float h = MathHelper.sqrt(f * f - g * g);
        if (g == 0.0f) {
            h = f;
        } else if (Math.abs(g) >= f) {
            return 0.0f;
        }
        return h * 0.5f;
    }

    private float getLeafRadiusForLayer(int i) {
        if (i < 0 || i >= 5) {
            return -1.0f;
        }
        if (i == 0 || i == 4) {
            return 2.0f;
        }
        return 3.0f;
    }

    private void makeLeaves(ModifiableTestableWorld world, Random random, BlockPos pos, Set<BlockPos> leaves, BlockBox box, BranchedTreeFeatureConfig config) {
        for (int i = 0; i < 5; ++i) {
            this.makeLeafLayer(world, random, pos.up(i), this.getLeafRadiusForLayer(i), leaves, box, config);
        }
    }

    private int makeOrCheckBranch(ModifiableTestableWorld world, Random random, BlockPos start, BlockPos end, boolean make, Set<BlockPos> logs, BlockBox blockBox, BranchedTreeFeatureConfig config) {
        if (!make && Objects.equals(start, end)) {
            return -1;
        }
        BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
        int i = this.getLongestSide(blockPos);
        float f = (float)blockPos.getX() / (float)i;
        float g = (float)blockPos.getY() / (float)i;
        float h = (float)blockPos.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockPos2 = start.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (make) {
                this.setBlockState(world, blockPos2, (BlockState)config.trunkProvider.getBlockState(random, blockPos2).with(PillarBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox);
                logs.add(blockPos2);
                continue;
            }
            if (LargeOakTreeFeature.canTreeReplace(world, blockPos2)) continue;
            return j;
        }
        return -1;
    }

    private int getLongestSide(BlockPos box) {
        int i = MathHelper.abs(box.getX());
        int j = MathHelper.abs(box.getY());
        int k = MathHelper.abs(box.getZ());
        if (k > i && k > j) {
            return k;
        }
        if (j > i) {
            return j;
        }
        return i;
    }

    private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
        int j;
        Direction.Axis axis = Direction.Axis.Y;
        int i = Math.abs(branchEnd.getX() - branchStart.getX());
        int k = Math.max(i, j = Math.abs(branchEnd.getZ() - branchStart.getZ()));
        if (k > 0) {
            if (i == k) {
                axis = Direction.Axis.X;
            } else if (j == k) {
                axis = Direction.Axis.Z;
            }
        }
        return axis;
    }

    private void makeLeaves(ModifiableTestableWorld world, Random random, int i, BlockPos pos, List<BranchPos> list, Set<BlockPos> set, BlockBox box, BranchedTreeFeatureConfig config) {
        for (BranchPos branchPos : list) {
            if (!this.isHighEnough(i, branchPos.getEndY() - pos.getY())) continue;
            this.makeLeaves(world, random, branchPos, set, box, config);
        }
    }

    private boolean isHighEnough(int treeHeight, int height) {
        return (double)height >= (double)treeHeight * 0.2;
    }

    private void makeTrunk(ModifiableTestableWorld world, Random random, BlockPos pos, int height, Set<BlockPos> set, BlockBox box, BranchedTreeFeatureConfig config) {
        this.makeOrCheckBranch(world, random, pos, pos.up(height), true, set, box, config);
    }

    private void makeBranches(ModifiableTestableWorld world, Random random, int treeHeight, BlockPos treePosition, List<BranchPos> branchPositions, Set<BlockPos> set, BlockBox box, BranchedTreeFeatureConfig config) {
        for (BranchPos branchPos : branchPositions) {
            int i = branchPos.getEndY();
            BlockPos blockPos = new BlockPos(treePosition.getX(), i, treePosition.getZ());
            if (blockPos.equals(branchPos) || !this.isHighEnough(treeHeight, i - treePosition.getY())) continue;
            this.makeOrCheckBranch(world, random, blockPos, branchPos, true, set, box, config);
        }
    }

    @Override
    public boolean generate(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BlockBox blockBox, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
        int m;
        Random random2 = new Random(random.nextLong());
        int i = this.getTreeHeight(modifiableTestableWorld, random, blockPos, 5 + random2.nextInt(12), set, blockBox, branchedTreeFeatureConfig);
        if (i == -1) {
            return false;
        }
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
        ArrayList<BranchPos> list = Lists.newArrayList();
        list.add(new BranchPos(blockPos.up(m), l));
        for (m = i - 5; m >= 0; --m) {
            float f = this.getBaseBranchSize(i, m);
            if (f < 0.0f) continue;
            for (int n = 0; n < k; ++n) {
                BlockPos blockPos3;
                double p;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
                double o = g * Math.sin(h = (double)(random2.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockPos2 = blockPos.add(o, (double)(m - 1), p = g * Math.cos(h) + 0.5);
                if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos2, blockPos3 = blockPos2.up(5), false, set, blockBox, branchedTreeFeatureConfig) != -1) continue;
                int q = blockPos.getX() - blockPos2.getX();
                int r = blockPos.getZ() - blockPos2.getZ();
                double s = (double)blockPos2.getY() - Math.sqrt(q * q + r * r) * 0.381;
                int t = s > (double)l ? l : (int)s;
                BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
                if (this.makeOrCheckBranch(modifiableTestableWorld, random, blockPos4, blockPos2, false, set, blockBox, branchedTreeFeatureConfig) != -1) continue;
                list.add(new BranchPos(blockPos2, blockPos4.getY()));
            }
        }
        this.makeLeaves(modifiableTestableWorld, random, i, blockPos, list, set2, blockBox, branchedTreeFeatureConfig);
        this.makeTrunk(modifiableTestableWorld, random, blockPos, j, set, blockBox, branchedTreeFeatureConfig);
        this.makeBranches(modifiableTestableWorld, random, i, blockPos, list, set, blockBox, branchedTreeFeatureConfig);
        return true;
    }

    private int getTreeHeight(ModifiableTestableWorld world, Random random, BlockPos pos, int height, Set<BlockPos> logs, BlockBox box, BranchedTreeFeatureConfig config) {
        if (!LargeOakTreeFeature.isDirtOrGrass(world, pos.down())) {
            return -1;
        }
        int i = this.makeOrCheckBranch(world, random, pos, pos.up(height - 1), false, logs, box, config);
        if (i == -1) {
            return height;
        }
        if (i < 6) {
            return -1;
        }
        return i;
    }

    static class BranchPos
    extends BlockPos {
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


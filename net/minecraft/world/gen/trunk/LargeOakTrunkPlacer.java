/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
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
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class LargeOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> LargeOakTrunkPlacer.fillTrunkPlacerFields(instance).apply(instance, LargeOakTrunkPlacer::new));
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
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        int n;
        int i = 5;
        int j = height + 2;
        int k = MathHelper.floor((double)j * 0.618);
        LargeOakTrunkPlacer.setToDirt(world, replacer, random, startPos.down(), config);
        double d = 1.0;
        int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
        int m = startPos.getY() + k;
        ArrayList<BranchPosition> list = Lists.newArrayList();
        list.add(new BranchPosition(startPos.up(n), m));
        for (n = j - 5; n >= 0; --n) {
            float f = LargeOakTrunkPlacer.shouldGenerateBranch(j, n);
            if (f < 0.0f) continue;
            for (int o = 0; o < l; ++o) {
                BlockPos blockPos2;
                double q;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
                double p = g * Math.sin(h = (double)(random.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockPos = startPos.add(p, (double)(n - 1), q = g * Math.cos(h) + 0.5);
                if (!this.makeOrCheckBranch(world, replacer, random, blockPos, blockPos2 = blockPos.up(5), false, config)) continue;
                int r = startPos.getX() - blockPos.getX();
                int s = startPos.getZ() - blockPos.getZ();
                double t = (double)blockPos.getY() - Math.sqrt(r * r + s * s) * 0.381;
                int u = t > (double)m ? m : (int)t;
                BlockPos blockPos3 = new BlockPos(startPos.getX(), u, startPos.getZ());
                if (!this.makeOrCheckBranch(world, replacer, random, blockPos3, blockPos, false, config)) continue;
                list.add(new BranchPosition(blockPos, blockPos3.getY()));
            }
        }
        this.makeOrCheckBranch(world, replacer, random, startPos, startPos.up(k), true, config);
        this.makeBranches(world, replacer, random, j, startPos, list, config);
        ArrayList<FoliagePlacer.TreeNode> list2 = Lists.newArrayList();
        for (BranchPosition branchPosition : list) {
            if (!this.isHighEnough(j, branchPosition.getEndY() - startPos.getY())) continue;
            list2.add(branchPosition.node);
        }
        return list2;
    }

    private boolean makeOrCheckBranch(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos startPos, BlockPos branchPos, boolean make, TreeFeatureConfig config) {
        if (!make && Objects.equals(startPos, branchPos)) {
            return true;
        }
        BlockPos blockPos = branchPos.add(-startPos.getX(), -startPos.getY(), -startPos.getZ());
        int i = this.getLongestSide(blockPos);
        float f = (float)blockPos.getX() / (float)i;
        float g = (float)blockPos.getY() / (float)i;
        float h = (float)blockPos.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockPos2 = startPos.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (make) {
                this.getAndSetState(testableWorld, biConsumer, random, blockPos2, config, state -> (BlockState)state.withIfExists(PillarBlock.AXIS, this.getLogAxis(startPos, blockPos2)));
                continue;
            }
            if (this.canReplaceOrIsLog(testableWorld, blockPos2)) continue;
            return false;
        }
        return true;
    }

    private int getLongestSide(BlockPos offset) {
        int i = MathHelper.abs(offset.getX());
        int j = MathHelper.abs(offset.getY());
        int k = MathHelper.abs(offset.getZ());
        return Math.max(i, Math.max(j, k));
    }

    private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
        int j;
        Direction.Axis axis = Direction.Axis.Y;
        int i = Math.abs(branchEnd.getX() - branchStart.getX());
        int k = Math.max(i, j = Math.abs(branchEnd.getZ() - branchStart.getZ()));
        if (k > 0) {
            axis = i == k ? Direction.Axis.X : Direction.Axis.Z;
        }
        return axis;
    }

    private boolean isHighEnough(int treeHeight, int height) {
        return (double)height >= (double)treeHeight * 0.2;
    }

    private void makeBranches(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int treeHeight, BlockPos startPos, List<BranchPosition> branchPositions, TreeFeatureConfig config) {
        for (BranchPosition branchPosition : branchPositions) {
            int i = branchPosition.getEndY();
            BlockPos blockPos = new BlockPos(startPos.getX(), i, startPos.getZ());
            if (blockPos.equals(branchPosition.node.getCenter()) || !this.isHighEnough(treeHeight, i - startPos.getY())) continue;
            this.makeOrCheckBranch(world, replacer, random, blockPos, branchPosition.node.getCenter(), true, config);
        }
    }

    /**
     * If the returned value is greater than or equal to 0, a branch will be generated.
     */
    private static float shouldGenerateBranch(int treeHeight, int height) {
        if ((float)height < (float)treeHeight * 0.3f) {
            return -1.0f;
        }
        float f = (float)treeHeight / 2.0f;
        float g = f - (float)height;
        float h = MathHelper.sqrt(f * f - g * g);
        if (g == 0.0f) {
            h = f;
        } else if (Math.abs(g) >= f) {
            return 0.0f;
        }
        return h * 0.5f;
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


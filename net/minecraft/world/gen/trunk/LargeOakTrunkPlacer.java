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
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
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
    public List<FoliagePlacer.TreeNode> generate(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, TreeFeatureConfig treeFeatureConfig) {
        int o;
        int j = 5;
        int k = i + 2;
        int l = MathHelper.floor((double)k * 0.618);
        LargeOakTrunkPlacer.setToDirt(testableWorld, biConsumer, random, blockPos.down(), treeFeatureConfig);
        double d = 1.0;
        int m = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)k / 13.0, 2.0)));
        int n = blockPos.getY() + l;
        ArrayList<BranchPosition> list = Lists.newArrayList();
        list.add(new BranchPosition(blockPos.up(o), n));
        for (o = k - 5; o >= 0; --o) {
            float f = LargeOakTrunkPlacer.shouldGenerateBranch(k, o);
            if (f < 0.0f) continue;
            for (int p = 0; p < m; ++p) {
                BlockPos blockPos3;
                double r;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
                double q = g * Math.sin(h = (double)(random.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockPos2 = blockPos.add(q, (double)(o - 1), r = g * Math.cos(h) + 0.5);
                if (!this.makeOrCheckBranch(testableWorld, biConsumer, random, blockPos2, blockPos3 = blockPos2.up(5), false, treeFeatureConfig)) continue;
                int s = blockPos.getX() - blockPos2.getX();
                int t = blockPos.getZ() - blockPos2.getZ();
                double u = (double)blockPos2.getY() - Math.sqrt(s * s + t * t) * 0.381;
                int v = u > (double)n ? n : (int)u;
                BlockPos blockPos4 = new BlockPos(blockPos.getX(), v, blockPos.getZ());
                if (!this.makeOrCheckBranch(testableWorld, biConsumer, random, blockPos4, blockPos2, false, treeFeatureConfig)) continue;
                list.add(new BranchPosition(blockPos2, blockPos4.getY()));
            }
        }
        this.makeOrCheckBranch(testableWorld, biConsumer, random, blockPos, blockPos.up(l), true, treeFeatureConfig);
        this.makeBranches(testableWorld, biConsumer, random, k, blockPos, list, treeFeatureConfig);
        ArrayList<FoliagePlacer.TreeNode> list2 = Lists.newArrayList();
        for (BranchPosition branchPosition : list) {
            if (!this.isHighEnough(k, branchPosition.getEndY() - blockPos.getY())) continue;
            list2.add(branchPosition.node);
        }
        return list2;
    }

    private boolean makeOrCheckBranch(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, BlockPos blockPos, BlockPos blockPos2, boolean bl, TreeFeatureConfig treeFeatureConfig) {
        if (!bl && Objects.equals(blockPos, blockPos2)) {
            return true;
        }
        BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        int i = this.getLongestSide(blockPos3);
        float f = (float)blockPos3.getX() / (float)i;
        float g = (float)blockPos3.getY() / (float)i;
        float h = (float)blockPos3.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockPos4 = blockPos.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (bl) {
                TrunkPlacer.getAndSetState(testableWorld, biConsumer, random, blockPos4, treeFeatureConfig, blockState -> (BlockState)blockState.with(PillarBlock.AXIS, this.getLogAxis(blockPos, blockPos4)));
                continue;
            }
            if (TreeFeature.canTreeReplace(testableWorld, blockPos4)) continue;
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

    private void makeBranches(TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, int i, BlockPos blockPos, List<BranchPosition> list, TreeFeatureConfig treeFeatureConfig) {
        for (BranchPosition branchPosition : list) {
            int j = branchPosition.getEndY();
            BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
            if (blockPos2.equals(branchPosition.node.getCenter()) || !this.isHighEnough(i, j - blockPos.getY())) continue;
            this.makeOrCheckBranch(testableWorld, biConsumer, random, blockPos2, branchPosition.node.getCenter(), true, treeFeatureConfig);
        }
    }

    /**
     * If the returned value is greater than or equal to 0, a branch will be generated.
     */
    private static float shouldGenerateBranch(int i, int j) {
        if ((float)j < (float)i * 0.3f) {
            return -1.0f;
        }
        float f = (float)i / 2.0f;
        float g = f - (float)j;
        float h = MathHelper.sqrt(f * f - g * g);
        if (g == 0.0f) {
            h = f;
        } else if (Math.abs(g) >= f) {
            return 0.0f;
        }
        return h * 0.5f;
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


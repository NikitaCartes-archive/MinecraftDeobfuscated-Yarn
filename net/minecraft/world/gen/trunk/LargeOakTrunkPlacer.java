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
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class LargeOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> LargeOakTrunkPlacer.method_28904(instance).apply(instance, LargeOakTrunkPlacer::new));

    public LargeOakTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig) {
        int n;
        int i = 5;
        int j = trunkHeight + 2;
        int k = MathHelper.floor((double)j * 0.618);
        if (!treeFeatureConfig.skipFluidCheck) {
            LargeOakTrunkPlacer.method_27400(world, pos.down());
        }
        double d = 1.0;
        int l = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)j / 13.0, 2.0)));
        int m = pos.getY() + k;
        ArrayList<BranchPosition> list = Lists.newArrayList();
        list.add(new BranchPosition(pos.up(n), m));
        for (n = j - 5; n >= 0; --n) {
            float f = this.method_27396(j, n);
            if (f < 0.0f) continue;
            for (int o = 0; o < l; ++o) {
                BlockPos blockPos2;
                double q;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
                double p = g * Math.sin(h = (double)(random.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos blockPos = pos.add(p, (double)(n - 1), q = g * Math.cos(h) + 0.5);
                if (!this.makeOrCheckBranch(world, random, blockPos, blockPos2 = blockPos.up(5), false, set, blockBox, treeFeatureConfig)) continue;
                int r = pos.getX() - blockPos.getX();
                int s = pos.getZ() - blockPos.getZ();
                double t = (double)blockPos.getY() - Math.sqrt(r * r + s * s) * 0.381;
                int u = t > (double)m ? m : (int)t;
                BlockPos blockPos3 = new BlockPos(pos.getX(), u, pos.getZ());
                if (!this.makeOrCheckBranch(world, random, blockPos3, blockPos, false, set, blockBox, treeFeatureConfig)) continue;
                list.add(new BranchPosition(blockPos, blockPos3.getY()));
            }
        }
        this.makeOrCheckBranch(world, random, pos, pos.up(k), true, set, blockBox, treeFeatureConfig);
        this.makeBranches(world, random, j, pos, list, set, blockBox, treeFeatureConfig);
        ArrayList<FoliagePlacer.TreeNode> list2 = Lists.newArrayList();
        for (BranchPosition branchPosition : list) {
            if (!this.isHighEnough(j, branchPosition.getEndY() - pos.getY())) continue;
            list2.add(branchPosition.node);
        }
        return list2;
    }

    private boolean makeOrCheckBranch(ModifiableTestableWorld world, Random random, BlockPos start, BlockPos end, boolean make, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig config) {
        if (!make && Objects.equals(start, end)) {
            return true;
        }
        BlockPos blockPos = end.add(-start.getX(), -start.getY(), -start.getZ());
        int i = this.getLongestSide(blockPos);
        float f = (float)blockPos.getX() / (float)i;
        float g = (float)blockPos.getY() / (float)i;
        float h = (float)blockPos.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos blockPos2 = start.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (make) {
                LargeOakTrunkPlacer.method_27404(world, blockPos2, (BlockState)config.trunkProvider.getBlockState(random, blockPos2).with(PillarBlock.AXIS, this.getLogAxis(start, blockPos2)), blockBox);
                set.add(blockPos2.toImmutable());
                continue;
            }
            if (TreeFeature.canTreeReplace(world, blockPos2)) continue;
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

    private void makeBranches(ModifiableTestableWorld world, Random random, int treeHeight, BlockPos treePos, List<BranchPosition> branches, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig config) {
        for (BranchPosition branchPosition : branches) {
            int i = branchPosition.getEndY();
            BlockPos blockPos = new BlockPos(treePos.getX(), i, treePos.getZ());
            if (blockPos.equals(branchPosition.node.getCenter()) || !this.isHighEnough(treeHeight, i - treePos.getY())) continue;
            this.makeOrCheckBranch(world, random, blockPos, branchPosition.node.getCenter(), true, set, blockBox, config);
        }
    }

    private float method_27396(int i, int j) {
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


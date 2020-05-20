/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class DarkOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> DarkOakTrunkPlacer.method_28904(instance).apply(instance, DarkOakTrunkPlacer::new));

    public DarkOakTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> method_28903() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig) {
        int r;
        int q;
        ArrayList<FoliagePlacer.TreeNode> list = Lists.newArrayList();
        BlockPos blockPos = pos.down();
        DarkOakTrunkPlacer.method_27400(world, blockPos);
        DarkOakTrunkPlacer.method_27400(world, blockPos.east());
        DarkOakTrunkPlacer.method_27400(world, blockPos.south());
        DarkOakTrunkPlacer.method_27400(world, blockPos.south().east());
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        int i = trunkHeight - random.nextInt(4);
        int j = 2 - random.nextInt(3);
        int k = pos.getX();
        int l = pos.getY();
        int m = pos.getZ();
        int n = k;
        int o = m;
        int p = l + trunkHeight - 1;
        for (q = 0; q < trunkHeight; ++q) {
            BlockPos blockPos2;
            if (q >= i && j > 0) {
                n += direction.getOffsetX();
                o += direction.getOffsetZ();
                --j;
            }
            if (!TreeFeature.isAirOrLeaves(world, blockPos2 = new BlockPos(n, r = l + q, o))) continue;
            DarkOakTrunkPlacer.method_27402(world, random, blockPos2, set, blockBox, treeFeatureConfig);
            DarkOakTrunkPlacer.method_27402(world, random, blockPos2.east(), set, blockBox, treeFeatureConfig);
            DarkOakTrunkPlacer.method_27402(world, random, blockPos2.south(), set, blockBox, treeFeatureConfig);
            DarkOakTrunkPlacer.method_27402(world, random, blockPos2.east().south(), set, blockBox, treeFeatureConfig);
        }
        list.add(new FoliagePlacer.TreeNode(new BlockPos(n, p, o), 0, true));
        for (q = -1; q <= 2; ++q) {
            for (r = -1; r <= 2; ++r) {
                if (q >= 0 && q <= 1 && r >= 0 && r <= 1 || random.nextInt(3) > 0) continue;
                int s = random.nextInt(3) + 2;
                for (int t = 0; t < s; ++t) {
                    DarkOakTrunkPlacer.method_27402(world, random, new BlockPos(k + q, p - t - 1, m + r), set, blockBox, treeFeatureConfig);
                }
                list.add(new FoliagePlacer.TreeNode(new BlockPos(n + q, p, o + r), 0, false));
            }
        }
        return list;
    }
}


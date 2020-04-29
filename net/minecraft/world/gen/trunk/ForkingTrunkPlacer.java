/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ForkingTrunkPlacer
extends TrunkPlacer {
    public ForkingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight, TrunkPlacerType.FORKING_TRUNK_PLACER);
    }

    public <T> ForkingTrunkPlacer(Dynamic<T> data) {
        this(data.get("base_height").asInt(0), data.get("height_rand_a").asInt(0), data.get("height_rand_b").asInt(0));
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox blockBox, TreeFeatureConfig treeFeatureConfig) {
        int o;
        ForkingTrunkPlacer.method_27400(world, pos.down());
        ArrayList<FoliagePlacer.TreeNode> list = Lists.newArrayList();
        Direction direction = Direction.Type.HORIZONTAL.random(random);
        int i = trunkHeight - random.nextInt(4) - 1;
        int j = 3 - random.nextInt(3);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int k = pos.getX();
        int l = pos.getZ();
        int m = 0;
        for (int n = 0; n < trunkHeight; ++n) {
            o = pos.getY() + n;
            if (n >= i && j > 0) {
                k += direction.getOffsetX();
                l += direction.getOffsetZ();
                --j;
            }
            if (!ForkingTrunkPlacer.method_27402(world, random, mutable.set(k, o, l), set, blockBox, treeFeatureConfig)) continue;
            m = o + 1;
        }
        list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 1, false));
        k = pos.getX();
        l = pos.getZ();
        Direction direction2 = Direction.Type.HORIZONTAL.random(random);
        if (direction2 != direction) {
            o = i - random.nextInt(2) - 1;
            int p = 1 + random.nextInt(3);
            m = 0;
            for (int q = o; q < trunkHeight && p > 0; ++q, --p) {
                if (q < 1) continue;
                int r = pos.getY() + q;
                if (!ForkingTrunkPlacer.method_27402(world, random, mutable.set(k += direction2.getOffsetX(), r, l += direction2.getOffsetZ()), set, blockBox, treeFeatureConfig)) continue;
                m = r + 1;
            }
            if (m > 1) {
                list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 0, false));
            }
        }
        return list;
    }
}


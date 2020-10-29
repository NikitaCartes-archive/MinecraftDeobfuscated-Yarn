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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ForkingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> ForkingTrunkPlacer.method_28904(instance).apply(instance, ForkingTrunkPlacer::new));

    public ForkingTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.FORKING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config) {
        int o;
        ForkingTrunkPlacer.setToDirt(world, pos.down());
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
            if (!ForkingTrunkPlacer.getAndSetState(world, random, mutable.set(k, o, l), placedStates, box, config)) continue;
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
                if (!ForkingTrunkPlacer.getAndSetState(world, random, mutable.set(k += direction2.getOffsetX(), r, l += direction2.getOffsetZ()), placedStates, box, config)) continue;
                m = r + 1;
            }
            if (m > 1) {
                list.add(new FoliagePlacer.TreeNode(new BlockPos(k, m, l), 0, false));
            }
        }
        return list;
    }
}


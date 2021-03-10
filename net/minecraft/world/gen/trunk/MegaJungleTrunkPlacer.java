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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class MegaJungleTrunkPlacer
extends GiantTrunkPlacer {
    public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> MegaJungleTrunkPlacer.fillTrunkPlacerFields(instance).apply(instance, MegaJungleTrunkPlacer::new));

    public MegaJungleTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> placedStates, BlockBox box, TreeFeatureConfig config) {
        ArrayList<FoliagePlacer.TreeNode> list = Lists.newArrayList();
        list.addAll(super.generate(world, random, trunkHeight, pos, placedStates, box, config));
        for (int i = trunkHeight - 2 - random.nextInt(4); i > trunkHeight / 2; i -= 2 + random.nextInt(4)) {
            float f = random.nextFloat() * ((float)Math.PI * 2);
            int j = 0;
            int k = 0;
            for (int l = 0; l < 5; ++l) {
                j = (int)(1.5f + MathHelper.cos(f) * (float)l);
                k = (int)(1.5f + MathHelper.sin(f) * (float)l);
                BlockPos blockPos = pos.add(j, i - 3 + l / 2, k);
                MegaJungleTrunkPlacer.getAndSetState(world, random, blockPos, placedStates, box, config);
            }
            list.add(new FoliagePlacer.TreeNode(pos.add(j, i, k), -2, false));
        }
        return list;
    }
}


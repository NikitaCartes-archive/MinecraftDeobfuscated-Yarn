/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DiskFeature
extends Feature<DiskFeatureConfig> {
    public DiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        DiskFeatureConfig diskFeatureConfig = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        boolean bl = false;
        int i = blockPos.getY();
        int j = i + diskFeatureConfig.halfHeight();
        int k = i - diskFeatureConfig.halfHeight() - 1;
        int l = diskFeatureConfig.radius().get(random);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-l, 0, -l), blockPos.add(l, 0, l))) {
            int n;
            int m = blockPos2.getX() - blockPos.getX();
            if (m * m + (n = blockPos2.getZ() - blockPos.getZ()) * n > l * l) continue;
            bl |= this.placeBlock(diskFeatureConfig, structureWorldAccess, random, j, k, mutable.set(blockPos2));
        }
        return bl;
    }

    protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, Random random, int topY, int bottomY, BlockPos.Mutable pos) {
        boolean bl = false;
        Object blockState = null;
        for (int i = topY; i > bottomY; --i) {
            pos.setY(i);
            if (!config.target().test(world, pos)) continue;
            BlockState blockState2 = config.stateProvider().getBlockState(world, random, pos);
            world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
            this.markBlocksAboveForPostProcessing(world, pos);
            bl = true;
        }
        return bl;
    }
}


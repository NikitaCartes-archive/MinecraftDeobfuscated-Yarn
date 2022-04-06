/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SurfaceDiskFeature
extends DiskFeature {
    public SurfaceDiskFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DiskFeatureConfig> context) {
        if (!context.getWorld().getBlockState(context.getOrigin().down()).isIn(context.getConfig().canOriginReplace())) {
            return false;
        }
        return super.generate(context);
    }

    @Override
    protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, int topY, int bottomY, BlockPos.Mutable pos) {
        if (!world.isAir(pos.setY(topY + 1))) {
            return false;
        }
        for (int i = topY; i > bottomY; --i) {
            BlockState blockState = world.getBlockState(pos.setY(i));
            if (this.anyTargetsMatch(config, blockState)) {
                world.setBlockState(pos, config.state(), Block.NOTIFY_LISTENERS);
                this.markBlocksAboveForPostProcessing(world, pos);
                return true;
            }
            if (blockState.isAir()) continue;
            return false;
        }
        return false;
    }
}


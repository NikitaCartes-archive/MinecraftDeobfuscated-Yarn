/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;

public class IcePatchFeature
extends DiskFeature {
    public IcePatchFeature(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<DiskFeatureConfig> arg) {
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        ChunkGenerator chunkGenerator = arg.method_33653();
        Random random = arg.method_33654();
        DiskFeatureConfig diskFeatureConfig = arg.method_33656();
        BlockPos blockPos = arg.method_33655();
        while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomSectionLimit() + 2) {
            blockPos = blockPos.down();
        }
        if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.SNOW_BLOCK)) {
            return false;
        }
        return super.generate(new class_5821<DiskFeatureConfig>(structureWorldAccess, chunkGenerator, random, blockPos, diskFeatureConfig));
    }
}


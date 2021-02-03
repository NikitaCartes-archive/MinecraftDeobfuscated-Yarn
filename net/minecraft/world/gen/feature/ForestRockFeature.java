/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class ForestRockFeature
extends Feature<SingleStateFeatureConfig> {
    public ForestRockFeature(Codec<SingleStateFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<SingleStateFeatureConfig> arg) {
        BlockState blockState;
        BlockPos blockPos = arg.method_33655();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        Random random = arg.method_33654();
        SingleStateFeatureConfig singleStateFeatureConfig = arg.method_33656();
        while (blockPos.getY() > structureWorldAccess.getBottomSectionLimit() + 3 && (structureWorldAccess.isAir(blockPos.down()) || !ForestRockFeature.isSoil(blockState = structureWorldAccess.getBlockState(blockPos.down())) && !ForestRockFeature.isStone(blockState))) {
            blockPos = blockPos.down();
        }
        if (blockPos.getY() <= structureWorldAccess.getBottomSectionLimit() + 3) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2);
            int k = random.nextInt(2);
            int l = random.nextInt(2);
            float f = (float)(j + k + l) * 0.333f + 0.5f;
            for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-j, -k, -l), blockPos.add(j, k, l))) {
                if (!(blockPos2.getSquaredDistance(blockPos) <= (double)(f * f))) continue;
                structureWorldAccess.setBlockState(blockPos2, singleStateFeatureConfig.state, 4);
            }
            blockPos = blockPos.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
        return true;
    }
}


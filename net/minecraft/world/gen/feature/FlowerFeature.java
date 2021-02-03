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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class FlowerFeature<U extends FeatureConfig>
extends Feature<U> {
    public FlowerFeature(Codec<U> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<U> arg) {
        Random random = arg.method_33654();
        BlockPos blockPos = arg.method_33655();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        U featureConfig = arg.method_33656();
        BlockState blockState = this.getFlowerState(random, blockPos, featureConfig);
        int i = 0;
        for (int j = 0; j < this.getFlowerAmount(featureConfig); ++j) {
            BlockPos blockPos2 = this.getPos(random, blockPos, featureConfig);
            if (!structureWorldAccess.isAir(blockPos2) || !blockState.canPlaceAt(structureWorldAccess, blockPos2) || !this.isPosValid(structureWorldAccess, blockPos2, featureConfig)) continue;
            structureWorldAccess.setBlockState(blockPos2, blockState, 2);
            ++i;
        }
        return i > 0;
    }

    public abstract boolean isPosValid(WorldAccess var1, BlockPos var2, U var3);

    public abstract int getFlowerAmount(U var1);

    public abstract BlockPos getPos(Random var1, BlockPos var2, U var3);

    public abstract BlockState getFlowerState(Random var1, BlockPos var2, U var3);
}


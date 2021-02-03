/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.class_5821;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NetherrackReplaceBlobsFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class NetherrackReplaceBlobsFeature
extends Feature<NetherrackReplaceBlobsFeatureConfig> {
    public NetherrackReplaceBlobsFeature(Codec<NetherrackReplaceBlobsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(class_5821<NetherrackReplaceBlobsFeatureConfig> arg) {
        NetherrackReplaceBlobsFeatureConfig netherrackReplaceBlobsFeatureConfig = arg.method_33656();
        StructureWorldAccess structureWorldAccess = arg.method_33652();
        Random random = arg.method_33654();
        Block block = netherrackReplaceBlobsFeatureConfig.target.getBlock();
        BlockPos blockPos = NetherrackReplaceBlobsFeature.method_27107(structureWorldAccess, arg.method_33655().mutableCopy().clamp(Direction.Axis.Y, structureWorldAccess.getBottomSectionLimit() + 1, structureWorldAccess.getTopHeightLimit() - 1), block);
        if (blockPos == null) {
            return false;
        }
        int i = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
        int j = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
        int k = netherrackReplaceBlobsFeatureConfig.getRadius().getValue(random);
        int l = Math.max(i, Math.max(j, k));
        boolean bl = false;
        for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, i, j, k)) {
            if (blockPos2.getManhattanDistance(blockPos) > l) break;
            BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
            if (!blockState.isOf(block)) continue;
            this.setBlockState(structureWorldAccess, blockPos2, netherrackReplaceBlobsFeatureConfig.state);
            bl = true;
        }
        return bl;
    }

    @Nullable
    private static BlockPos method_27107(WorldAccess worldAccess, BlockPos.Mutable mutable, Block block) {
        while (mutable.getY() > worldAccess.getBottomSectionLimit() + 1) {
            BlockState blockState = worldAccess.getBlockState(mutable);
            if (blockState.isOf(block)) {
                return mutable;
            }
            mutable.move(Direction.DOWN);
        }
        return null;
    }
}


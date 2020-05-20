/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndGatewayFeature
extends Feature<EndGatewayFeatureConfig> {
    public EndGatewayFeature(Codec<EndGatewayFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, EndGatewayFeatureConfig endGatewayFeatureConfig) {
        for (BlockPos blockPos22 : BlockPos.iterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
            boolean bl4;
            boolean bl = blockPos22.getX() == blockPos.getX();
            boolean bl2 = blockPos22.getY() == blockPos.getY();
            boolean bl3 = blockPos22.getZ() == blockPos.getZ();
            boolean bl5 = bl4 = Math.abs(blockPos22.getY() - blockPos.getY()) == 2;
            if (bl && bl2 && bl3) {
                BlockPos blockPos3 = blockPos22.toImmutable();
                this.setBlockState(serverWorldAccess, blockPos3, Blocks.END_GATEWAY.getDefaultState());
                endGatewayFeatureConfig.getExitPos().ifPresent(blockPos2 -> {
                    BlockEntity blockEntity = serverWorldAccess.getBlockEntity(blockPos3);
                    if (blockEntity instanceof EndGatewayBlockEntity) {
                        EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
                        endGatewayBlockEntity.setExitPortalPos((BlockPos)blockPos2, endGatewayFeatureConfig.isExact());
                        blockEntity.markDirty();
                    }
                });
                continue;
            }
            if (bl2) {
                this.setBlockState(serverWorldAccess, blockPos22, Blocks.AIR.getDefaultState());
                continue;
            }
            if (bl4 && bl && bl3) {
                this.setBlockState(serverWorldAccess, blockPos22, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            if (!bl && !bl3 || bl4) {
                this.setBlockState(serverWorldAccess, blockPos22, Blocks.AIR.getDefaultState());
                continue;
            }
            this.setBlockState(serverWorldAccess, blockPos22, Blocks.BEDROCK.getDefaultState());
        }
        return true;
    }
}


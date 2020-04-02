/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndGatewayFeature
extends Feature<EndGatewayFeatureConfig> {
    public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, EndGatewayFeatureConfig endGatewayFeatureConfig) {
        for (BlockPos blockPos22 : BlockPos.iterate(blockPos.add(-1, -2, -1), blockPos.add(1, 2, 1))) {
            boolean bl4;
            boolean bl = blockPos22.getX() == blockPos.getX();
            boolean bl2 = blockPos22.getY() == blockPos.getY();
            boolean bl3 = blockPos22.getZ() == blockPos.getZ();
            boolean bl5 = bl4 = Math.abs(blockPos22.getY() - blockPos.getY()) == 2;
            if (bl && bl2 && bl3) {
                BlockPos blockPos3 = blockPos22.toImmutable();
                this.setBlockState(iWorld, blockPos3, Blocks.END_GATEWAY.getDefaultState());
                endGatewayFeatureConfig.getExitPos().ifPresent(blockPos2 -> {
                    BlockEntity blockEntity = iWorld.getBlockEntity(blockPos3);
                    if (blockEntity instanceof EndGatewayBlockEntity) {
                        EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
                        endGatewayBlockEntity.setExitPortalPos((BlockPos)blockPos2, endGatewayFeatureConfig.isExact());
                        blockEntity.markDirty();
                    }
                });
                continue;
            }
            if (bl2) {
                this.setBlockState(iWorld, blockPos22, Blocks.AIR.getDefaultState());
                continue;
            }
            if (bl4 && bl && bl3) {
                this.setBlockState(iWorld, blockPos22, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            if (!bl && !bl3 || bl4) {
                this.setBlockState(iWorld, blockPos22, Blocks.AIR.getDefaultState());
                continue;
            }
            this.setBlockState(iWorld, blockPos22, Blocks.BEDROCK.getDefaultState());
        }
        return true;
    }
}


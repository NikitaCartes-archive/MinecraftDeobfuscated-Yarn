/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.class_4576;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderer
extends class_4576<PistonBlockEntity> {
    private final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();

    protected void method_3576(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
        BlockState blockState = pistonBlockEntity.getPushedBlock();
        if (blockState.isAir() || pistonBlockEntity.getProgress(g) >= 1.0f) {
            return;
        }
        BlockModelRenderer.enableBrightnessCache();
        bufferBuilder.setOffset(d - (double)blockPos.getX() + (double)pistonBlockEntity.getRenderOffsetX(g), e - (double)blockPos.getY() + (double)pistonBlockEntity.getRenderOffsetY(g), f - (double)blockPos.getZ() + (double)pistonBlockEntity.getRenderOffsetZ(g));
        World world = this.getWorld();
        if (blockState.getBlock() == Blocks.PISTON_HEAD && pistonBlockEntity.getProgress(g) <= 4.0f) {
            blockState = (BlockState)blockState.with(PistonHeadBlock.SHORT, true);
            this.method_3575(blockPos, blockState, bufferBuilder, world, false);
        } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
            PistonType pistonType = blockState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState2 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, pistonType)).with(PistonHeadBlock.FACING, blockState.get(PistonBlock.FACING));
            blockState2 = (BlockState)blockState2.with(PistonHeadBlock.SHORT, pistonBlockEntity.getProgress(g) >= 0.5f);
            this.method_3575(blockPos, blockState2, bufferBuilder, world, false);
            BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
            bufferBuilder.setOffset(d - (double)blockPos2.getX(), e - (double)blockPos2.getY(), f - (double)blockPos2.getZ());
            blockState = (BlockState)blockState.with(PistonBlock.EXTENDED, true);
            this.method_3575(blockPos2, blockState, bufferBuilder, world, true);
        } else {
            this.method_3575(blockPos, blockState, bufferBuilder, world, false);
        }
        BlockModelRenderer.disableBrightnessCache();
    }

    private boolean method_3575(BlockPos blockPos, BlockState blockState, BufferBuilder bufferBuilder, World world, boolean bl) {
        return this.manager.getModelRenderer().tesselate(world, this.manager.getModel(blockState), blockState, blockPos, bufferBuilder, bl, new Random(), blockState.getRenderingSeed(blockPos));
    }
}


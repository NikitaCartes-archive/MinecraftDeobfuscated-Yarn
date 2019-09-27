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
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderer
extends BlockEntityRenderer<PistonBlockEntity> {
    private final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();

    public PistonBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3576(PistonBlockEntity pistonBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        World world = pistonBlockEntity.getWorld();
        if (world == null) {
            return;
        }
        BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
        BlockState blockState = pistonBlockEntity.getPushedBlock();
        if (blockState.isAir() || pistonBlockEntity.getProgress(g) >= 1.0f) {
            return;
        }
        BlockModelRenderer.enableBrightnessCache();
        arg.method_22903();
        arg.method_22904((float)(-(blockPos.getX() & 0xF)) + pistonBlockEntity.getRenderOffsetX(g), (float)(-(blockPos.getY() & 0xF)) + pistonBlockEntity.getRenderOffsetY(g), (float)(-(blockPos.getZ() & 0xF)) + pistonBlockEntity.getRenderOffsetZ(g));
        if (blockState.getBlock() == Blocks.PISTON_HEAD && pistonBlockEntity.getProgress(g) <= 4.0f) {
            blockState = (BlockState)blockState.with(PistonHeadBlock.SHORT, true);
            this.method_3575(blockPos, blockState, arg, arg2, world, false);
        } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
            PistonType pistonType = blockState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState2 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, pistonType)).with(PistonHeadBlock.FACING, blockState.get(PistonBlock.FACING));
            blockState2 = (BlockState)blockState2.with(PistonHeadBlock.SHORT, pistonBlockEntity.getProgress(g) >= 0.5f);
            this.method_3575(blockPos, blockState2, arg, arg2, world, false);
            BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
            arg.method_22909();
            arg.method_22904(-(blockPos2.getX() & 0xF), -(blockPos2.getY() & 0xF), -(blockPos2.getZ() & 0xF));
            blockState = (BlockState)blockState.with(PistonBlock.EXTENDED, true);
            this.method_3575(blockPos2, blockState, arg, arg2, world, true);
            arg.method_22903();
        } else {
            this.method_3575(blockPos, blockState, arg, arg2, world, false);
        }
        arg.method_22909();
        BlockModelRenderer.disableBrightnessCache();
    }

    private void method_3575(BlockPos blockPos, BlockState blockState, class_4587 arg, class_4597 arg2, World world, boolean bl) {
        BlockRenderLayer blockRenderLayer = BlockRenderLayer.method_22715(blockState);
        class_4588 lv = arg2.getBuffer(blockRenderLayer);
        this.manager.getModelRenderer().tesselate(world, this.manager.getModel(blockState), blockState, blockPos, arg, lv, bl, new Random(), blockState.getRenderingSeed(blockPos));
        MinecraftClient.getInstance().getBlockRenderManager().tesselateBlock(blockState, blockPos, world, arg, lv, bl, new Random());
    }
}


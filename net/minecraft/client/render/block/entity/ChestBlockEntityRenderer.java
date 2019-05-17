/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ChestEntityModel;
import net.minecraft.client.render.entity.model.LargeChestEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity>
extends BlockEntityRenderer<T> {
    private static final Identifier TRAPPED_DOUBLE_TEX = new Identifier("textures/entity/chest/trapped_double.png");
    private static final Identifier CHRISTMAS_DOUBLE_TEX = new Identifier("textures/entity/chest/christmas_double.png");
    private static final Identifier NORMAL_DOUBLE_TEX = new Identifier("textures/entity/chest/normal_double.png");
    private static final Identifier TRAPPED_TEX = new Identifier("textures/entity/chest/trapped.png");
    private static final Identifier CHRISTMAS_TEX = new Identifier("textures/entity/chest/christmas.png");
    private static final Identifier NORMAL_TEX = new Identifier("textures/entity/chest/normal.png");
    private static final Identifier ENDER_TEX = new Identifier("textures/entity/chest/ender.png");
    private final ChestEntityModel modelSingleChest = new ChestEntityModel();
    private final ChestEntityModel modelDoubleChest = new LargeChestEntityModel();
    private boolean isChristmas;

    public ChestBlockEntityRenderer() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
    }

    @Override
    public void render(T blockEntity, double d, double e, double f, float g, int i) {
        ChestType chestType;
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        BlockState blockState = ((BlockEntity)blockEntity).hasWorld() ? ((BlockEntity)blockEntity).getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType chestType2 = chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        if (chestType == ChestType.LEFT) {
            return;
        }
        boolean bl = chestType != ChestType.SINGLE;
        ChestEntityModel chestEntityModel = this.method_3562(blockEntity, i, bl);
        if (i >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(bl ? 8.0f : 4.0f, 4.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translatef((float)d, (float)e + 1.0f, (float)f + 1.0f);
        GlStateManager.scalef(1.0f, -1.0f, -1.0f);
        float h = blockState.get(ChestBlock.FACING).asRotation();
        if ((double)Math.abs(h) > 1.0E-5) {
            GlStateManager.translatef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(h, 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        }
        this.method_3561(blockEntity, g, chestEntityModel);
        chestEntityModel.method_2799();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (i >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private ChestEntityModel method_3562(T blockEntity, int i, boolean bl) {
        Identifier identifier = i >= 0 ? DESTROY_STAGE_TEXTURES[i] : (this.isChristmas ? (bl ? CHRISTMAS_DOUBLE_TEX : CHRISTMAS_TEX) : (blockEntity instanceof TrappedChestBlockEntity ? (bl ? TRAPPED_DOUBLE_TEX : TRAPPED_TEX) : (blockEntity instanceof EnderChestBlockEntity ? ENDER_TEX : (bl ? NORMAL_DOUBLE_TEX : NORMAL_TEX))));
        this.bindTexture(identifier);
        return bl ? this.modelDoubleChest : this.modelSingleChest;
    }

    private void method_3561(T blockEntity, float f, ChestEntityModel chestEntityModel) {
        float g = ((ChestAnimationProgress)blockEntity).getAnimationProgress(f);
        g = 1.0f - g;
        g = 1.0f - g * g * g;
        chestEntityModel.method_2798().pitch = -(g * 1.5707964f);
    }
}


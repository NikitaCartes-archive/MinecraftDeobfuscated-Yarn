/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends BlockEntityRenderer<SignBlockEntity> {
    private static final Identifier OAK_TEX = new Identifier("textures/entity/signs/oak.png");
    private static final Identifier SPRUCE_TEX = new Identifier("textures/entity/signs/spruce.png");
    private static final Identifier BIRCH_TEX = new Identifier("textures/entity/signs/birch.png");
    private static final Identifier ACACIA_TEX = new Identifier("textures/entity/signs/acacia.png");
    private static final Identifier JUNGLE_TEX = new Identifier("textures/entity/signs/jungle.png");
    private static final Identifier DARK_OAK_TEX = new Identifier("textures/entity/signs/dark_oak.png");
    private final SignBlockEntityModel model = new SignBlockEntityModel();

    public void method_3582(SignBlockEntity signBlockEntity, double d, double e, double f, float g, int i) {
        BlockState blockState = signBlockEntity.getCachedState();
        GlStateManager.pushMatrix();
        float h = 0.6666667f;
        if (blockState.getBlock() instanceof SignBlock) {
            GlStateManager.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
            GlStateManager.rotatef(-((float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f), 0.0f, 1.0f, 0.0f);
            this.model.getSignpostModel().visible = true;
        } else if (blockState.getBlock() instanceof WallSignBlock) {
            GlStateManager.translatef((float)d + 0.5f, (float)e + 0.5f, (float)f + 0.5f);
            GlStateManager.rotatef(-blockState.get(WallSignBlock.FACING).asRotation(), 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.3125f, -0.4375f);
            this.model.getSignpostModel().visible = false;
        } else {
            GlStateManager.popMatrix();
            return;
        }
        if (i >= 0) {
            this.bindTexture(DESTROY_STAGE_TEXTURES[i]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(this.getModelTexture(blockState.getBlock()));
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6666667f, -0.6666667f, -0.6666667f);
        this.model.render();
        GlStateManager.popMatrix();
        TextRenderer textRenderer = this.getFontRenderer();
        float j = 0.010416667f;
        GlStateManager.translatef(0.0f, 0.33333334f, 0.046666667f);
        GlStateManager.scalef(0.010416667f, -0.010416667f, 0.010416667f);
        GlStateManager.normal3f(0.0f, 0.0f, -0.010416667f);
        GlStateManager.depthMask(false);
        int k = signBlockEntity.getTextColor().getSignColor();
        if (i < 0) {
            for (int l = 0; l < 4; ++l) {
                String string = signBlockEntity.getTextBeingEditedOnRow(l, component -> {
                    List<Component> list = TextComponentUtil.wrapLines(component, 90, textRenderer, false, true);
                    return list.isEmpty() ? "" : list.get(0).getFormattedText();
                });
                if (string == null) continue;
                textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, l * 10 - signBlockEntity.text.length * 5, k);
                if (l != signBlockEntity.getCurrentRow() || signBlockEntity.getSelectionStart() < 0) continue;
                int m = textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(signBlockEntity.getSelectionStart(), string.length()), 0)));
                int n = textRenderer.isRightToLeft() ? -1 : 1;
                int o = (m - textRenderer.getStringWidth(string) / 2) * n;
                int p = l * 10 - signBlockEntity.text.length * 5;
                if (signBlockEntity.isCaretVisible()) {
                    if (signBlockEntity.getSelectionStart() < string.length()) {
                        DrawableHelper.fill(o, p - 1, o + 1, p + textRenderer.fontHeight, 0xFF000000 | k);
                    } else {
                        textRenderer.draw("_", o, p, k);
                    }
                }
                if (signBlockEntity.getSelectionEnd() == signBlockEntity.getSelectionStart()) continue;
                int q = Math.min(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
                int r = Math.max(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
                int s = (textRenderer.getStringWidth(string.substring(0, q)) - textRenderer.getStringWidth(string) / 2) * n;
                int t = (textRenderer.getStringWidth(string.substring(0, r)) - textRenderer.getStringWidth(string) / 2) * n;
                this.method_16210(Math.min(s, t), p, Math.max(s, t), p + textRenderer.fontHeight);
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        if (i >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private Identifier getModelTexture(Block block) {
        if (block == Blocks.OAK_SIGN || block == Blocks.OAK_WALL_SIGN) {
            return OAK_TEX;
        }
        if (block == Blocks.SPRUCE_SIGN || block == Blocks.SPRUCE_WALL_SIGN) {
            return SPRUCE_TEX;
        }
        if (block == Blocks.BIRCH_SIGN || block == Blocks.BIRCH_WALL_SIGN) {
            return BIRCH_TEX;
        }
        if (block == Blocks.ACACIA_SIGN || block == Blocks.ACACIA_WALL_SIGN) {
            return ACACIA_TEX;
        }
        if (block == Blocks.JUNGLE_SIGN || block == Blocks.JUNGLE_WALL_SIGN) {
            return JUNGLE_TEX;
        }
        if (block == Blocks.DARK_OAK_SIGN || block == Blocks.DARK_OAK_WALL_SIGN) {
            return DARK_OAK_TEX;
        }
        return OAK_TEX;
    }

    private void method_16210(int i, int j, int k, int l) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex(i, l, 0.0).next();
        bufferBuilder.vertex(k, l, 0.0).next();
        bufferBuilder.vertex(k, j, 0.0).next();
        bufferBuilder.vertex(i, j, 0.0).next();
        tessellator.draw();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
}


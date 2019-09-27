/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends BlockEntityRenderer<SignBlockEntity> {
    private final ModelPart field_20990 = new ModelPart(64, 32, 0, 0);
    private final ModelPart field_20991;

    public SignBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.field_20990.addCuboid(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f, 0.0f);
        this.field_20991 = new ModelPart(64, 32, 0, 14);
        this.field_20991.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f, 0.0f);
    }

    public void method_23083(SignBlockEntity signBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        BlockState blockState = signBlockEntity.getCachedState();
        arg.method_22903();
        float h = 0.6666667f;
        if (blockState.getBlock() instanceof SignBlock) {
            arg.method_22904(0.5, 0.5, 0.5);
            arg.method_22907(Vector3f.field_20705.method_23214(-((float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f), true));
            this.field_20991.visible = true;
        } else {
            arg.method_22904(0.5, 0.5, 0.5);
            arg.method_22907(Vector3f.field_20705.method_23214(-blockState.get(WallSignBlock.FACING).asRotation(), true));
            arg.method_22904(0.0, -0.3125, -0.4375);
            this.field_20991.visible = false;
        }
        Sprite sprite = this.method_23082(this.getModelTexture(blockState.getBlock()));
        arg.method_22903();
        arg.method_22905(0.6666667f, -0.6666667f, -0.6666667f);
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
        this.field_20990.method_22698(arg, lv, 0.0625f, i, sprite);
        this.field_20991.method_22698(arg, lv, 0.0625f, i, sprite);
        arg.method_22909();
        TextRenderer textRenderer = this.field_20989.getFontRenderer();
        float j = 0.010416667f;
        arg.method_22904(0.0, 0.3333333432674408, 0.046666666865348816);
        arg.method_22905(0.010416667f, -0.010416667f, 0.010416667f);
        int k = signBlockEntity.getTextColor().getSignColor();
        for (int l = 0; l < 4; ++l) {
            String string = signBlockEntity.getTextBeingEditedOnRow(l, text -> {
                List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
                return list.isEmpty() ? "" : list.get(0).asFormattedString();
            });
            if (string == null) continue;
            float m = -textRenderer.getStringWidth(string) / 2;
            textRenderer.method_22942(string, m, l * 10 - signBlockEntity.text.length * 5, k, false, arg.method_22910(), arg2, false, 0, i);
            if (l != signBlockEntity.getCurrentRow() || signBlockEntity.getSelectionStart() < 0) continue;
            int n = textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(signBlockEntity.getSelectionStart(), string.length()), 0)));
            int o = textRenderer.isRightToLeft() ? -1 : 1;
            int p = (n - textRenderer.getStringWidth(string) / 2) * o;
            int q = l * 10 - signBlockEntity.text.length * 5;
            if (signBlockEntity.isCaretVisible()) {
                if (signBlockEntity.getSelectionStart() < string.length()) {
                    DrawableHelper.fill(p, q - 1, p + 1, q + textRenderer.fontHeight, 0xFF000000 | k);
                } else {
                    textRenderer.method_22942("_", p, q, k, false, arg.method_22910(), arg2, false, 0, i);
                }
            }
            if (signBlockEntity.getSelectionEnd() == signBlockEntity.getSelectionStart()) continue;
            int r = Math.min(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
            int s = Math.max(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
            int t = (textRenderer.getStringWidth(string.substring(0, r)) - textRenderer.getStringWidth(string) / 2) * o;
            int u = (textRenderer.getStringWidth(string.substring(0, s)) - textRenderer.getStringWidth(string) / 2) * o;
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(arg.method_22910());
            this.method_16210(Math.min(t, u), q, Math.max(t, u), q + textRenderer.fontHeight);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.popMatrix();
        }
        arg.method_22909();
    }

    private Identifier getModelTexture(Block block) {
        if (block == Blocks.OAK_SIGN || block == Blocks.OAK_WALL_SIGN) {
            return ModelLoader.field_21014;
        }
        if (block == Blocks.SPRUCE_SIGN || block == Blocks.SPRUCE_WALL_SIGN) {
            return ModelLoader.field_21015;
        }
        if (block == Blocks.BIRCH_SIGN || block == Blocks.BIRCH_WALL_SIGN) {
            return ModelLoader.field_21016;
        }
        if (block == Blocks.ACACIA_SIGN || block == Blocks.ACACIA_WALL_SIGN) {
            return ModelLoader.field_21017;
        }
        if (block == Blocks.JUNGLE_SIGN || block == Blocks.JUNGLE_WALL_SIGN) {
            return ModelLoader.field_21018;
        }
        if (block == Blocks.DARK_OAK_SIGN || block == Blocks.DARK_OAK_WALL_SIGN) {
            return ModelLoader.field_21019;
        }
        return ModelLoader.field_21014;
    }

    private void method_16210(int i, int j, int k, int l) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        RenderSystem.color4f(0.0f, 0.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, VertexFormats.POSITION);
        bufferBuilder.vertex(i, l, 0.0).next();
        bufferBuilder.vertex(k, l, 0.0).next();
        bufferBuilder.vertex(k, j, 0.0).next();
        bufferBuilder.vertex(i, j, 0.0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
}


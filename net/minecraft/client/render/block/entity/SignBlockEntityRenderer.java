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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

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

    public void method_23083(SignBlockEntity signBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j) {
        float k;
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();
        float h = 0.6666667f;
        if (blockState.getBlock() instanceof SignBlock) {
            matrixStack.translate(0.5, 0.5, 0.5);
            k = -((float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
            this.field_20991.visible = true;
        } else {
            matrixStack.translate(0.5, 0.5, 0.5);
            k = -blockState.get(WallSignBlock.FACING).asRotation();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
            matrixStack.translate(0.0, -0.3125, -0.4375);
            this.field_20991.visible = false;
        }
        Sprite sprite = this.getSprite(this.getModelTexture(blockState.getBlock()));
        matrixStack.push();
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        this.field_20990.render(matrixStack, vertexConsumer, 0.0625f, i, j, sprite);
        this.field_20991.render(matrixStack, vertexConsumer, 0.0625f, i, j, sprite);
        matrixStack.pop();
        TextRenderer textRenderer = this.field_20989.getFontRenderer();
        float l = 0.010416667f;
        matrixStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        matrixStack.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int m = signBlockEntity.getTextColor().getSignColor();
        for (int n = 0; n < 4; ++n) {
            String string = signBlockEntity.getTextBeingEditedOnRow(n, text -> {
                List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
                return list.isEmpty() ? "" : list.get(0).asFormattedString();
            });
            if (string == null) continue;
            float o = -textRenderer.getStringWidth(string) / 2;
            textRenderer.method_22942(string, o, n * 10 - signBlockEntity.text.length * 5, m, false, matrixStack.peek(), layeredVertexConsumerStorage, false, 0, i);
            if (n != signBlockEntity.getCurrentRow() || signBlockEntity.getSelectionStart() < 0) continue;
            int p = textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(signBlockEntity.getSelectionStart(), string.length()), 0)));
            int q = textRenderer.isRightToLeft() ? -1 : 1;
            int r = (p - textRenderer.getStringWidth(string) / 2) * q;
            int s = n * 10 - signBlockEntity.text.length * 5;
            if (signBlockEntity.isCaretVisible()) {
                if (signBlockEntity.getSelectionStart() < string.length()) {
                    DrawableHelper.fill(r, s - 1, r + 1, s + textRenderer.fontHeight, 0xFF000000 | m);
                } else {
                    textRenderer.method_22942("_", r, s, m, false, matrixStack.peek(), layeredVertexConsumerStorage, false, 0, i);
                }
            }
            if (signBlockEntity.getSelectionEnd() == signBlockEntity.getSelectionStart()) continue;
            int t = Math.min(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
            int u = Math.max(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
            int v = (textRenderer.getStringWidth(string.substring(0, t)) - textRenderer.getStringWidth(string) / 2) * q;
            int w = (textRenderer.getStringWidth(string.substring(0, u)) - textRenderer.getStringWidth(string) / 2) * q;
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.peek());
            this.method_16210(Math.min(v, w), s, Math.max(v, w), s + textRenderer.fontHeight);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.popMatrix();
        }
        matrixStack.pop();
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


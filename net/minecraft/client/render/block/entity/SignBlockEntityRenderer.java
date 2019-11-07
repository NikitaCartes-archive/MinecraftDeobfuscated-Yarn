/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends BlockEntityRenderer<SignBlockEntity> {
    private final class_4702 field_21529 = new class_4702();

    public SignBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_23083(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float h;
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();
        float g = 0.6666667f;
        if (blockState.getBlock() instanceof SignBlock) {
            matrixStack.translate(0.5, 0.5, 0.5);
            h = -((float)(blockState.get(SignBlock.ROTATION) * 360) / 16.0f);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h));
            this.field_21529.field_21531.visible = true;
        } else {
            matrixStack.translate(0.5, 0.5, 0.5);
            h = -blockState.get(WallSignBlock.FACING).asRotation();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h));
            matrixStack.translate(0.0, -0.3125, -0.4375);
            this.field_21529.field_21531.visible = false;
        }
        Sprite sprite = this.getSprite(SignBlockEntityRenderer.getModelTexture(blockState.getBlock()));
        matrixStack.push();
        matrixStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        this.field_21529.field_21530.render(matrixStack, vertexConsumer, i, j, sprite);
        this.field_21529.field_21531.render(matrixStack, vertexConsumer, i, j, sprite);
        matrixStack.pop();
        TextRenderer textRenderer = this.blockEntityRenderDispatcher.getTextRenderer();
        float k = 0.010416667f;
        matrixStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        matrixStack.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int l = signBlockEntity.getTextColor().getSignColor();
        for (int m = 0; m < 4; ++m) {
            String string = signBlockEntity.getTextBeingEditedOnRow(m, text -> {
                List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
                return list.isEmpty() ? "" : list.get(0).asFormattedString();
            });
            if (string == null) continue;
            float n = -textRenderer.getStringWidth(string) / 2;
            textRenderer.draw(string, n, m * 10 - signBlockEntity.text.length * 5, l, false, matrixStack.method_23760().method_23761(), vertexConsumerProvider, false, 0, i);
        }
        matrixStack.pop();
    }

    public static Identifier getModelTexture(Block block) {
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

    @Environment(value=EnvType.CLIENT)
    public static final class class_4702
    extends Model {
        public final ModelPart field_21530 = new ModelPart(64, 32, 0, 0);
        public final ModelPart field_21531;

        public class_4702() {
            super(RenderLayer::getEntityCutoutNoCull);
            this.field_21530.addCuboid(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f, 0.0f);
            this.field_21531 = new ModelPart(64, 32, 0, 14);
            this.field_21531.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f, 0.0f);
        }

        @Override
        public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
            this.field_21530.render(matrixStack, vertexConsumer, i, j, null, f, g, h);
            this.field_21531.render(matrixStack, vertexConsumer, i, j, null, f, g, h);
        }
    }
}


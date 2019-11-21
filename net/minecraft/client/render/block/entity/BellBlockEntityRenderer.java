/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.class_4730;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockEntityRenderer
extends BlockEntityRenderer<BellBlockEntity> {
    public static final class_4730 BELL_BODY_TEXTURE = new class_4730(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/bell/bell_body"));
    private final ModelPart field_20816 = new ModelPart(32, 32, 0, 0);

    public BellBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.field_20816.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f);
        this.field_20816.setPivot(8.0f, 12.0f, 8.0f);
        ModelPart modelPart = new ModelPart(32, 32, 0, 13);
        modelPart.addCuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f);
        modelPart.setPivot(-8.0f, -12.0f, -8.0f);
        this.field_20816.addChild(modelPart);
    }

    @Override
    public void render(BellBlockEntity bellBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float g = (float)bellBlockEntity.ringTicks + f;
        float h = 0.0f;
        float k = 0.0f;
        if (bellBlockEntity.isRinging) {
            float l = MathHelper.sin(g / (float)Math.PI) / (4.0f + g / 3.0f);
            if (bellBlockEntity.lastSideHit == Direction.NORTH) {
                h = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
                h = l;
            } else if (bellBlockEntity.lastSideHit == Direction.EAST) {
                k = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.WEST) {
                k = l;
            }
        }
        this.field_20816.pitch = h;
        this.field_20816.roll = k;
        VertexConsumer vertexConsumer = BELL_BODY_TEXTURE.method_24145(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.field_20816.render(matrixStack, vertexConsumer, i, j);
    }
}


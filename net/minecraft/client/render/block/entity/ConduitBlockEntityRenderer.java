/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
extends BlockEntityRenderer<ConduitBlockEntity> {
    public static final SpriteIdentifier BASE_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/base"));
    public static final SpriteIdentifier CAGE_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/cage"));
    public static final SpriteIdentifier WIND_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/wind"));
    public static final SpriteIdentifier WIND_VERTICAL_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/wind_vertical"));
    public static final SpriteIdentifier OPEN_EYE_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/open_eye"));
    public static final SpriteIdentifier CLOSED_EYE_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/conduit/closed_eye"));
    private final ModelPart field_20823 = new ModelPart(16, 16, 0, 0);
    private final ModelPart field_20824;
    private final ModelPart field_20825;
    private final ModelPart field_20826;

    public ConduitBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.field_20823.addCuboid(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, 0.01f);
        this.field_20824 = new ModelPart(64, 32, 0, 0);
        this.field_20824.addCuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.field_20825 = new ModelPart(32, 16, 0, 0);
        this.field_20825.addCuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.field_20826 = new ModelPart(32, 16, 0, 0);
        this.field_20826.addCuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
    }

    @Override
    public void render(ConduitBlockEntity conduitBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float g = (float)conduitBlockEntity.ticks + f;
        if (!conduitBlockEntity.isActive()) {
            float h = conduitBlockEntity.getRotation(0.0f);
            VertexConsumer vertexConsumer = BASE_TEX.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
            this.field_20825.render(matrixStack, vertexConsumer, i, j);
            matrixStack.pop();
            return;
        }
        float h = conduitBlockEntity.getRotation(f) * 57.295776f;
        float k = MathHelper.sin(g * 0.1f) / 2.0f + 0.5f;
        k = k * k + k;
        matrixStack.push();
        matrixStack.translate(0.5, 0.3f + k * 0.2f, 0.5);
        Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f);
        vector3f.normalize();
        matrixStack.multiply(new Quaternion(vector3f, h, true));
        this.field_20826.render(matrixStack, CAGE_TEX.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
        int l = conduitBlockEntity.ticks / 66 % 3;
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        if (l == 1) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        } else if (l == 2) {
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
        VertexConsumer vertexConsumer2 = (l == 1 ? WIND_VERTICAL_TEX : WIND_TEX).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.field_20824.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        this.field_20824.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        Camera camera = this.dispatcher.camera;
        matrixStack.push();
        matrixStack.translate(0.5, 0.3f + k * 0.2f, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        float m = -camera.getYaw();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(m));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        float n = 1.3333334f;
        matrixStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.field_20823.render(matrixStack, (conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEX : CLOSED_EYE_TEX).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
    }
}


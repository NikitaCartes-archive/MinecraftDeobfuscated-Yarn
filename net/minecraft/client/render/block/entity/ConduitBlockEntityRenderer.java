/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
implements BlockEntityRenderer<ConduitBlockEntity> {
    public static final SpriteIdentifier BASE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/base"));
    public static final SpriteIdentifier CAGE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/cage"));
    public static final SpriteIdentifier WIND_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/wind"));
    public static final SpriteIdentifier WIND_VERTICAL_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/wind_vertical"));
    public static final SpriteIdentifier OPEN_EYE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/open_eye"));
    public static final SpriteIdentifier CLOSED_EYE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/closed_eye"));
    private final ModelPart field_20823;
    private final ModelPart field_20824;
    private final ModelPart field_20825;
    private final ModelPart field_20826;
    private final BlockEntityRenderDispatcher field_27753;

    public ConduitBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.field_27753 = context.getRenderDispatcher();
        this.field_20823 = context.getLayerModelPart(EntityModelLayers.CONDUIT_EYE);
        this.field_20824 = context.getLayerModelPart(EntityModelLayers.CONDUIT_WIND);
        this.field_20825 = context.getLayerModelPart(EntityModelLayers.CONDUIT_SHELL);
        this.field_20826 = context.getLayerModelPart(EntityModelLayers.CONDUIT);
    }

    public static class_5607 method_32150() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("eye", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, new class_5605(0.01f)), class_5603.field_27701);
        return class_5607.method_32110(lv, 16, 16);
    }

    public static class_5607 method_32151() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("wind", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 64, 32);
    }

    public static class_5607 method_32152() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("shell", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 32, 16);
    }

    public static class_5607 method_32153() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("shell", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 32, 16);
    }

    @Override
    public void render(ConduitBlockEntity conduitBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        float g = (float)conduitBlockEntity.ticks + f;
        if (!conduitBlockEntity.isActive()) {
            float h = conduitBlockEntity.getRotation(0.0f);
            VertexConsumer vertexConsumer = BASE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
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
        this.field_20826.render(matrixStack, CAGE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
        int l = conduitBlockEntity.ticks / 66 % 3;
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        if (l == 1) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        } else if (l == 2) {
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
        VertexConsumer vertexConsumer2 = (l == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.field_20824.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        this.field_20824.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        Camera camera = this.field_27753.camera;
        matrixStack.push();
        matrixStack.translate(0.5, 0.3f + k * 0.2f, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        float m = -camera.getYaw();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(m));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        float n = 1.3333334f;
        matrixStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.field_20823.render(matrixStack, (conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
    }
}


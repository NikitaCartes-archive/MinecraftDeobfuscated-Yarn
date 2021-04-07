/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityRenderer
extends EntityRenderer<EndCrystalEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private static final float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483);
    private static final String GLASS = "glass";
    private static final String BASE = "base";
    private final ModelPart core;
    private final ModelPart frame;
    private final ModelPart bottom;

    public EndCrystalEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
        this.frame = modelPart.getChild(GLASS);
        this.core = modelPart.getChild(EntityModelPartNames.CUBE);
        this.bottom = modelPart.getChild(BASE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(GLASS, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 16).cuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = EndCrystalEntityRenderer.getYOffset(endCrystalEntity, g);
        float j = ((float)endCrystalEntity.endCrystalAge + g) * 3.0f;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0, -0.5, 0.0);
        int k = OverlayTexture.DEFAULT_UV;
        if (endCrystalEntity.shouldShowBottom()) {
            this.bottom.render(matrixStack, vertexConsumer, i, k);
        }
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        matrixStack.translate(0.0, 1.5f + h / 2.0f, 0.0);
        matrixStack.multiply(new Quaternion(new Vec3f(SINE_45_DEGREES, 0.0f, SINE_45_DEGREES), 60.0f, true));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        float l = 0.875f;
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vec3f(SINE_45_DEGREES, 0.0f, SINE_45_DEGREES), 60.0f, true));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        this.frame.render(matrixStack, vertexConsumer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternion(new Vec3f(SINE_45_DEGREES, 0.0f, SINE_45_DEGREES), 60.0f, true));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        this.core.render(matrixStack, vertexConsumer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        BlockPos blockPos = endCrystalEntity.getBeamTarget();
        if (blockPos != null) {
            float m = (float)blockPos.getX() + 0.5f;
            float n = (float)blockPos.getY() + 0.5f;
            float o = (float)blockPos.getZ() + 0.5f;
            float p = (float)((double)m - endCrystalEntity.getX());
            float q = (float)((double)n - endCrystalEntity.getY());
            float r = (float)((double)o - endCrystalEntity.getZ());
            matrixStack.translate(p, q, r);
            EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, endCrystalEntity.endCrystalAge, matrixStack, vertexConsumerProvider, i);
        }
        super.render(endCrystalEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public static float getYOffset(EndCrystalEntity crystal, float tickDelta) {
        float f = (float)crystal.endCrystalAge + tickDelta;
        float g = MathHelper.sin(f * 0.2f) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f;
    }

    @Override
    public Identifier getTexture(EndCrystalEntity endCrystalEntity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
    }
}


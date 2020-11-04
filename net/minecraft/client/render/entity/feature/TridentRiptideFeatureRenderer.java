/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident_riptide.png");
    private final ModelPart aura;

    public TridentRiptideFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext, class_5599 arg) {
        super(featureRendererContext);
        ModelPart modelPart = arg.method_32072(EntityModelLayers.SPIN_ATTACK);
        this.aura = modelPart.method_32086("box");
    }

    public static class_5607 method_32200() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("box", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -16.0f, -8.0f, 16.0f, 32.0f, 16.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (!((LivingEntity)livingEntity).isUsingRiptide()) {
            return;
        }
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        for (int m = 0; m < 3; ++m) {
            matrixStack.push();
            float n = j * (float)(-(45 + m * 5));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(n));
            float o = 0.75f * (float)m;
            matrixStack.scale(o, o, o);
            matrixStack.translate(0.0, -0.2f + 0.6f * (float)m, 0.0);
            this.aura.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }
    }
}


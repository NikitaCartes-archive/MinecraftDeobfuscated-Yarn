/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer
extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4188(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, IronGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l, float m) {
        if (ironGolemEntity.method_6502() == 0) {
            return;
        }
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(5.0f + 180.0f * ((IronGolemEntityModel)this.getModel()).getRightArm().pitch / (float)Math.PI));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0f));
        matrixStack.translate(0.6875, -0.3125, 1.0625);
        float n = 0.5f;
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0f));
        matrixStack.translate(-0.5, -0.5, 0.5);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.POPPY.getDefaultState(), matrixStack, layeredVertexConsumerStorage, i, OverlayTexture.field_21444);
        matrixStack.pop();
    }
}


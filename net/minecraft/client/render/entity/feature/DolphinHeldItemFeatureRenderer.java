/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer
extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_17160(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, DolphinEntity dolphinEntity, float f, float g, float h, float j, float k, float l, float m) {
        boolean bl = dolphinEntity.getMainArm() == Arm.RIGHT;
        matrixStack.push();
        float n = 1.0f;
        float o = -1.0f;
        float p = MathHelper.abs(dolphinEntity.pitch) / 60.0f;
        if (dolphinEntity.pitch < 0.0f) {
            matrixStack.translate(0.0, 1.0f - p * 0.5f, -1.0f + p * 0.5f);
        } else {
            matrixStack.translate(0.0, 1.0f + p * 0.8f, -1.0f + p * 0.2f);
        }
        ItemStack itemStack = bl ? dolphinEntity.getMainHandStack() : dolphinEntity.getOffHandStack();
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(dolphinEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }
}


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
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, WitchEntityModel<T>> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4208(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getMainHandStack();
        if (itemStack.getItem() != Items.POTION) {
            return;
        }
        matrixStack.push();
        ((WitchEntityModel)this.getModel()).getHead().rotate(matrixStack, 0.0625f);
        ((WitchEntityModel)this.getModel()).getNose().rotate(matrixStack, 0.0625f);
        matrixStack.translate(0.0, 0.375, -0.03125);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-35.0f));
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, false, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity>
extends VillagerHeldItemFeatureRenderer<T, WitchEntityModel<T>> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<T, WitchEntityModel<T>> featureRendererContext, HeldItemRenderer heldItemRenderer) {
        super(featureRendererContext, heldItemRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = ((LivingEntity)livingEntity).getMainHandStack();
        matrixStack.push();
        if (itemStack.isOf(Items.POTION)) {
            ((WitchEntityModel)this.getContextModel()).getHead().rotate(matrixStack);
            ((WitchEntityModel)this.getContextModel()).getNose().rotate(matrixStack);
            matrixStack.translate(0.0625f, 0.25f, 0.0f);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(140.0f));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10.0f));
            matrixStack.translate(0.0f, -0.4f, 0.4f);
        }
        super.render(matrixStack, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
        matrixStack.pop();
    }
}


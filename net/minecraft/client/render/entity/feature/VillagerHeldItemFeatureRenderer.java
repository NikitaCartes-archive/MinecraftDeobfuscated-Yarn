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
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity>
extends FeatureRenderer<T, VillagerResemblingModel<T>> {
    public VillagerHeldItemFeatureRenderer(FeatureRendererContext<T, VillagerResemblingModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_18147(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.4f, -0.4f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0f, true));
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.MAINHAND);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.GROUND, false, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }
}


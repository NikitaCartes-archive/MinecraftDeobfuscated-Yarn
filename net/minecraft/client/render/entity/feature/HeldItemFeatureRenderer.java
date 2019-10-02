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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public HeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_17162(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
        ItemStack itemStack2;
        boolean bl = ((LivingEntity)livingEntity).getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? ((LivingEntity)livingEntity).getOffHandStack() : ((LivingEntity)livingEntity).getMainHandStack();
        ItemStack itemStack3 = itemStack2 = bl ? ((LivingEntity)livingEntity).getMainHandStack() : ((LivingEntity)livingEntity).getOffHandStack();
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return;
        }
        matrixStack.push();
        if (((EntityModel)this.getModel()).isChild) {
            float n = 0.5f;
            matrixStack.translate(0.0, 0.75, 0.0);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        }
        this.method_4192((LivingEntity)livingEntity, itemStack2, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, layeredVertexConsumerStorage);
        this.method_4192((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }

    private void method_4192(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, Arm arm, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        if (itemStack.isEmpty()) {
            return;
        }
        matrixStack.push();
        ((ModelWithArms)this.getModel()).setArmAngle(0.0625f, arm, matrixStack);
        if (livingEntity.isInSneakingPose()) {
            matrixStack.translate(0.0, 0.2f, 0.0);
        }
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f, true));
        boolean bl = arm == Arm.LEFT;
        matrixStack.translate((float)(bl ? -1 : 1) / 16.0f, 0.125, -0.625);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, type, bl, matrixStack, layeredVertexConsumerStorage);
        matrixStack.pop();
    }
}


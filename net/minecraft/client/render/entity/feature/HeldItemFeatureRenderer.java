/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class HeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public HeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        ItemStack itemStack2;
        boolean bl = ((LivingEntity)livingEntity).getMainArm() == Arm.RIGHT;
        ItemStack itemStack = bl ? ((LivingEntity)livingEntity).getOffHandStack() : ((LivingEntity)livingEntity).getMainHandStack();
        ItemStack itemStack3 = itemStack2 = bl ? ((LivingEntity)livingEntity).getMainHandStack() : ((LivingEntity)livingEntity).getOffHandStack();
        if (itemStack.isEmpty() && itemStack2.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        if (((EntityModel)this.getContextModel()).child) {
            float m = 0.5f;
            GlStateManager.translatef(0.0f, 0.75f, 0.0f);
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        }
        this.method_4192((LivingEntity)livingEntity, itemStack2, ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT);
        this.method_4192((LivingEntity)livingEntity, itemStack, ModelTransformation.Type.THIRD_PERSON_LEFT_HAND, Arm.LEFT);
        GlStateManager.popMatrix();
    }

    private void method_4192(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, Arm arm) {
        if (itemStack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        this.method_4193(arm);
        if (livingEntity.isInSneakingPose()) {
            GlStateManager.translatef(0.0f, 0.2f, 0.0f);
        }
        GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        boolean bl = arm == Arm.LEFT;
        GlStateManager.translatef((float)(bl ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItemFromSide(livingEntity, itemStack, type, bl);
        GlStateManager.popMatrix();
    }

    protected void method_4193(Arm arm) {
        ((ModelWithArms)this.getContextModel()).setArmAngle(0.0625f, arm);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}


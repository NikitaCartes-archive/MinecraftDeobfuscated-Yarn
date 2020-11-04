/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SmallTropicalFishEntityModel<T extends Entity>
extends TintableCompositeModel<T> {
    private final ModelPart field_27522;
    private final ModelPart field_27523;

    public SmallTropicalFishEntityModel(ModelPart modelPart) {
        this.field_27522 = modelPart;
        this.field_27523 = modelPart.method_32086("tail");
    }

    public static class_5607 method_32060(class_5605 arg) {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 22;
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32098(-1.0f, -1.5f, -3.0f, 2.0f, 3.0f, 6.0f, arg), class_5603.method_32090(0.0f, 22.0f, 0.0f));
        lv2.method_32117("tail", class_5606.method_32108().method_32101(22, -6).method_32098(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 6.0f, arg), class_5603.method_32090(0.0f, 22.0f, 3.0f));
        lv2.method_32117("right_fin", class_5606.method_32108().method_32101(2, 16).method_32098(-2.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, arg), class_5603.method_32091(-1.0f, 22.5f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        lv2.method_32117("left_fin", class_5606.method_32108().method_32101(2, 12).method_32098(0.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, arg), class_5603.method_32091(1.0f, 22.5f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        lv2.method_32117("top_fin", class_5606.method_32108().method_32101(10, -5).method_32098(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f, arg), class_5603.method_32090(0.0f, 20.5f, -3.0f));
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27522;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.field_27523.yaw = -f * 0.45f * MathHelper.sin(0.6f * animationProgress);
    }
}


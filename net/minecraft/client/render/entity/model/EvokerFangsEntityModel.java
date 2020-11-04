/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27414;
    private final ModelPart field_3374;
    private final ModelPart field_3376;
    private final ModelPart field_3375;

    public EvokerFangsEntityModel(ModelPart modelPart) {
        this.field_27414 = modelPart;
        this.field_3374 = modelPart.method_32086("base");
        this.field_3376 = modelPart.method_32086("upper_jaw");
        this.field_3375 = modelPart.method_32086("lower_jaw");
    }

    public static class_5607 method_31998() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("base", class_5606.method_32108().method_32101(0, 0).method_32097(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), class_5603.method_32090(-5.0f, 24.0f, -5.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(40, 0).method_32097(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        lv2.method_32117("upper_jaw", lv3, class_5603.method_32090(1.5f, 24.0f, -4.0f));
        lv2.method_32117("lower_jaw", lv3, class_5603.method_32091(-1.5f, 24.0f, 4.0f, 0.0f, (float)Math.PI, 0.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = limbAngle * 2.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        f = 1.0f - f * f * f;
        this.field_3376.roll = (float)Math.PI - f * 0.35f * (float)Math.PI;
        this.field_3375.roll = (float)Math.PI + f * 0.35f * (float)Math.PI;
        float g = (limbAngle + MathHelper.sin(limbAngle * 2.7f)) * 0.6f * 12.0f;
        this.field_3375.pivotY = this.field_3376.pivotY = 24.0f - g;
        this.field_3374.pivotY = this.field_3376.pivotY;
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27414;
    }
}


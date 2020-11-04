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
public class CodEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27405;
    private final ModelPart tail;

    public CodEntityModel(ModelPart modelPart) {
        this.field_27405 = modelPart;
        this.tail = modelPart.method_32086("tail_fin");
    }

    public static class_5607 method_31989() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 22;
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32097(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f), class_5603.method_32090(0.0f, 22.0f, 0.0f));
        lv2.method_32117("head", class_5606.method_32108().method_32101(11, 0).method_32097(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), class_5603.method_32090(0.0f, 22.0f, 0.0f));
        lv2.method_32117("nose", class_5606.method_32108().method_32101(0, 0).method_32097(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f), class_5603.method_32090(0.0f, 22.0f, -3.0f));
        lv2.method_32117("right_fin", class_5606.method_32108().method_32101(22, 1).method_32097(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), class_5603.method_32091(-1.0f, 23.0f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        lv2.method_32117("left_fin", class_5606.method_32108().method_32101(22, 4).method_32097(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), class_5603.method_32091(1.0f, 23.0f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        lv2.method_32117("tail_fin", class_5606.method_32108().method_32101(22, 3).method_32097(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f), class_5603.method_32090(0.0f, 22.0f, 7.0f));
        lv2.method_32117("top_fin", class_5606.method_32108().method_32101(20, -6).method_32097(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f), class_5603.method_32090(0.0f, 20.0f, 0.0f));
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27405;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.tail.yaw = -f * 0.45f * MathHelper.sin(0.6f * animationProgress);
    }
}


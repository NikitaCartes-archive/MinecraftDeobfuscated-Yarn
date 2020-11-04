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
public class SalmonEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27494;
    private final ModelPart tail;

    public SalmonEntityModel(ModelPart modelPart) {
        this.field_27494 = modelPart;
        this.tail = modelPart.method_32086("body_back");
    }

    public static class_5607 method_32036() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 20;
        class_5610 lv3 = lv2.method_32117("body_front", class_5606.method_32108().method_32101(0, 0).method_32097(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), class_5603.method_32090(0.0f, 20.0f, 0.0f));
        class_5610 lv4 = lv2.method_32117("body_back", class_5606.method_32108().method_32101(0, 13).method_32097(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), class_5603.method_32090(0.0f, 20.0f, 8.0f));
        lv2.method_32117("head", class_5606.method_32108().method_32101(22, 0).method_32097(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), class_5603.method_32090(0.0f, 20.0f, 0.0f));
        lv4.method_32117("back_fin", class_5606.method_32108().method_32101(20, 10).method_32097(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f), class_5603.method_32090(0.0f, 0.0f, 8.0f));
        lv3.method_32117("top_front_fin", class_5606.method_32108().method_32101(2, 1).method_32097(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f), class_5603.method_32090(0.0f, -4.5f, 5.0f));
        lv4.method_32117("top_back_fin", class_5606.method_32108().method_32101(0, 2).method_32097(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f), class_5603.method_32090(0.0f, -4.5f, -1.0f));
        lv2.method_32117("right_fin", class_5606.method_32108().method_32101(-4, 0).method_32097(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), class_5603.method_32091(-1.5f, 21.5f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        lv2.method_32117("left_fin", class_5606.method_32108().method_32101(0, 0).method_32097(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), class_5603.method_32091(1.5f, 21.5f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27494;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        float g = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.3f;
            g = 1.7f;
        }
        this.tail.yaw = -f * 0.25f * MathHelper.sin(g * 0.6f * animationProgress);
    }
}


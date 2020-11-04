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
public class SmallPufferfishEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27473;
    private final ModelPart field_27474;
    private final ModelPart field_27475;

    public SmallPufferfishEntityModel(ModelPart modelPart) {
        this.field_27473 = modelPart;
        this.field_27474 = modelPart.method_32086("left_fin");
        this.field_27475 = modelPart.method_32086("right_fin");
    }

    public static class_5607 method_32032() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 23;
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 27).method_32097(-1.5f, -2.0f, -1.5f, 3.0f, 2.0f, 3.0f), class_5603.method_32090(0.0f, 23.0f, 0.0f));
        lv2.method_32117("right_eye", class_5606.method_32108().method_32101(24, 6).method_32097(-1.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), class_5603.method_32090(0.0f, 20.0f, 0.0f));
        lv2.method_32117("left_eye", class_5606.method_32108().method_32101(28, 6).method_32097(0.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), class_5603.method_32090(0.0f, 20.0f, 0.0f));
        lv2.method_32117("back_fin", class_5606.method_32108().method_32101(-3, 0).method_32097(-1.5f, 0.0f, 0.0f, 3.0f, 0.0f, 3.0f), class_5603.method_32090(0.0f, 22.0f, 1.5f));
        lv2.method_32117("right_fin", class_5606.method_32108().method_32101(25, 0).method_32097(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), class_5603.method_32090(-1.5f, 22.0f, -1.5f));
        lv2.method_32117("left_fin", class_5606.method_32108().method_32101(25, 0).method_32097(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), class_5603.method_32090(1.5f, 22.0f, -1.5f));
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27473;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.field_27475.roll = -0.2f + 0.4f * MathHelper.sin(animationProgress * 0.2f);
        this.field_27474.roll = 0.2f - 0.4f * MathHelper.sin(animationProgress * 0.2f);
    }
}


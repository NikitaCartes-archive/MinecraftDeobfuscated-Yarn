/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27500;
    private final ModelPart field_27501;
    private final ModelPart topSnowball;
    private final ModelPart field_27502;
    private final ModelPart field_27503;

    public SnowGolemEntityModel(ModelPart modelPart) {
        this.field_27500 = modelPart;
        this.topSnowball = modelPart.method_32086("head");
        this.field_27502 = modelPart.method_32086("left_arm");
        this.field_27503 = modelPart.method_32086("right_arm");
        this.field_27501 = modelPart.method_32086("upper_body");
    }

    public static class_5607 method_32053() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        float f = 4.0f;
        class_5605 lv3 = new class_5605(-0.5f);
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, lv3), class_5603.method_32090(0.0f, 4.0f, 0.0f));
        class_5606 lv4 = class_5606.method_32108().method_32101(32, 0).method_32098(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, lv3);
        lv2.method_32117("left_arm", lv4, class_5603.method_32091(5.0f, 6.0f, 1.0f, 0.0f, 0.0f, 1.0f));
        lv2.method_32117("right_arm", lv4, class_5603.method_32091(-5.0f, 6.0f, -1.0f, 0.0f, (float)Math.PI, -1.0f));
        lv2.method_32117("upper_body", class_5606.method_32108().method_32101(0, 16).method_32098(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, lv3), class_5603.method_32090(0.0f, 13.0f, 0.0f));
        lv2.method_32117("lower_body", class_5606.method_32108().method_32101(0, 36).method_32098(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, lv3), class_5603.method_32090(0.0f, 24.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.topSnowball.yaw = headYaw * ((float)Math.PI / 180);
        this.topSnowball.pitch = headPitch * ((float)Math.PI / 180);
        this.field_27501.yaw = headYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin(this.field_27501.yaw);
        float g = MathHelper.cos(this.field_27501.yaw);
        this.field_27502.yaw = this.field_27501.yaw;
        this.field_27503.yaw = this.field_27501.yaw + (float)Math.PI;
        this.field_27502.pivotX = g * 5.0f;
        this.field_27502.pivotZ = -f * 5.0f;
        this.field_27503.pivotX = -g * 5.0f;
        this.field_27503.pivotZ = f * 5.0f;
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27500;
    }

    public ModelPart getTopSnowball() {
        return this.topSnowball;
    }
}


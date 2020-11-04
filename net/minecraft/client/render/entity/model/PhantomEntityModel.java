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
public class PhantomEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27463;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart tail;
    private final ModelPart lowerTail;

    public PhantomEntityModel(ModelPart modelPart) {
        this.field_27463 = modelPart;
        ModelPart modelPart2 = modelPart.method_32086("body");
        this.tail = modelPart2.method_32086("tail_base");
        this.lowerTail = this.tail.method_32086("tail_tip");
        this.leftWing = modelPart2.method_32086("left_wing_base");
        this.leftWingTip = this.leftWing.method_32086("left_wing_tip");
        this.rightWing = modelPart2.method_32086("right_wing_base");
        this.rightWingTip = this.rightWing.method_32086("right_wing_tip");
    }

    public static class_5607 method_32024() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("body", class_5606.method_32108().method_32101(0, 8).method_32097(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f), class_5603.method_32092(-0.1f, 0.0f, 0.0f));
        class_5610 lv4 = lv3.method_32117("tail_base", class_5606.method_32108().method_32101(3, 20).method_32097(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f), class_5603.method_32090(0.0f, -2.0f, 1.0f));
        lv4.method_32117("tail_tip", class_5606.method_32108().method_32101(4, 29).method_32097(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f), class_5603.method_32090(0.0f, 0.5f, 6.0f));
        class_5610 lv5 = lv3.method_32117("left_wing_base", class_5606.method_32108().method_32101(23, 12).method_32097(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), class_5603.method_32091(2.0f, -2.0f, -8.0f, 0.0f, 0.0f, 0.1f));
        lv5.method_32117("left_wing_tip", class_5606.method_32108().method_32101(16, 24).method_32097(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), class_5603.method_32091(6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f));
        class_5610 lv6 = lv3.method_32117("right_wing_base", class_5606.method_32108().method_32101(23, 12).method_32096().method_32097(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), class_5603.method_32091(-3.0f, -2.0f, -8.0f, 0.0f, 0.0f, -0.1f));
        lv6.method_32117("right_wing_tip", class_5606.method_32108().method_32101(16, 24).method_32096().method_32097(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), class_5603.method_32091(-6.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f));
        lv3.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f), class_5603.method_32091(0.0f, 1.0f, -7.0f, 0.2f, 0.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27463;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = ((float)(((Entity)entity).getEntityId() * 3) + animationProgress) * 0.13f;
        float g = 16.0f;
        this.leftWing.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.leftWingTip.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.tail.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.lowerTail.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}


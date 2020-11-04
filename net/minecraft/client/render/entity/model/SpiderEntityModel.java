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
public class SpiderEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27504;
    private final ModelPart head;
    private final ModelPart field_27505;
    private final ModelPart field_27506;
    private final ModelPart field_27507;
    private final ModelPart field_27508;
    private final ModelPart field_27509;
    private final ModelPart field_27510;
    private final ModelPart field_27511;
    private final ModelPart field_27512;

    public SpiderEntityModel(ModelPart modelPart) {
        this.field_27504 = modelPart;
        this.head = modelPart.method_32086("head");
        this.field_27505 = modelPart.method_32086("right_hind_leg");
        this.field_27506 = modelPart.method_32086("left_hind_leg");
        this.field_27507 = modelPart.method_32086("right_middle_hind_leg");
        this.field_27508 = modelPart.method_32086("left_middle_hind_leg");
        this.field_27509 = modelPart.method_32086("right_middle_front_leg");
        this.field_27510 = modelPart.method_32086("left_middle_front_leg");
        this.field_27511 = modelPart.method_32086("right_front_leg");
        this.field_27512 = modelPart.method_32086("left_front_leg");
    }

    public static class_5607 method_32054() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 15;
        lv2.method_32117("head", class_5606.method_32108().method_32101(32, 4).method_32097(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f), class_5603.method_32090(0.0f, 15.0f, -3.0f));
        lv2.method_32117("body0", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), class_5603.method_32090(0.0f, 15.0f, 0.0f));
        lv2.method_32117("body1", class_5606.method_32108().method_32101(0, 12).method_32097(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f), class_5603.method_32090(0.0f, 15.0f, 9.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(18, 0).method_32097(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        class_5606 lv4 = class_5606.method_32108().method_32101(18, 0).method_32097(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-4.0f, 15.0f, 2.0f));
        lv2.method_32117("left_hind_leg", lv4, class_5603.method_32090(4.0f, 15.0f, 2.0f));
        lv2.method_32117("right_middle_hind_leg", lv3, class_5603.method_32090(-4.0f, 15.0f, 1.0f));
        lv2.method_32117("left_middle_hind_leg", lv4, class_5603.method_32090(4.0f, 15.0f, 1.0f));
        lv2.method_32117("right_middle_front_leg", lv3, class_5603.method_32090(-4.0f, 15.0f, 0.0f));
        lv2.method_32117("left_middle_front_leg", lv4, class_5603.method_32090(4.0f, 15.0f, 0.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-4.0f, 15.0f, -1.0f));
        lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(4.0f, 15.0f, -1.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27504;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        float f = 0.7853982f;
        this.field_27505.roll = -0.7853982f;
        this.field_27506.roll = 0.7853982f;
        this.field_27507.roll = -0.58119464f;
        this.field_27508.roll = 0.58119464f;
        this.field_27509.roll = -0.58119464f;
        this.field_27510.roll = 0.58119464f;
        this.field_27511.roll = -0.7853982f;
        this.field_27512.roll = 0.7853982f;
        float g = -0.0f;
        float h = 0.3926991f;
        this.field_27505.yaw = 0.7853982f;
        this.field_27506.yaw = -0.7853982f;
        this.field_27507.yaw = 0.3926991f;
        this.field_27508.yaw = -0.3926991f;
        this.field_27509.yaw = -0.3926991f;
        this.field_27510.yaw = 0.3926991f;
        this.field_27511.yaw = -0.7853982f;
        this.field_27512.yaw = 0.7853982f;
        float i = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 0.0f) * 0.4f) * limbDistance;
        float j = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * limbDistance;
        float k = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * limbDistance;
        float l = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 4.712389f) * 0.4f) * limbDistance;
        float m = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 0.0f) * 0.4f) * limbDistance;
        float n = Math.abs(MathHelper.sin(limbAngle * 0.6662f + (float)Math.PI) * 0.4f) * limbDistance;
        float o = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 1.5707964f) * 0.4f) * limbDistance;
        float p = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 4.712389f) * 0.4f) * limbDistance;
        this.field_27505.yaw += i;
        this.field_27506.yaw += -i;
        this.field_27507.yaw += j;
        this.field_27508.yaw += -j;
        this.field_27509.yaw += k;
        this.field_27510.yaw += -k;
        this.field_27511.yaw += l;
        this.field_27512.yaw += -l;
        this.field_27505.roll += m;
        this.field_27506.roll += -m;
        this.field_27507.roll += n;
        this.field_27508.roll += -n;
        this.field_27509.roll += o;
        this.field_27510.roll += -o;
        this.field_27511.roll += p;
        this.field_27512.roll += -p;
    }
}


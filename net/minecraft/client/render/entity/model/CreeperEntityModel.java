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
public class CreeperEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27406;
    private final ModelPart head;
    private final ModelPart field_27407;
    private final ModelPart field_27408;
    private final ModelPart field_27409;
    private final ModelPart field_27410;

    public CreeperEntityModel(ModelPart modelPart) {
        this.field_27406 = modelPart;
        this.head = modelPart.method_32086("head");
        this.field_27408 = modelPart.method_32086("right_hind_leg");
        this.field_27407 = modelPart.method_32086("left_hind_leg");
        this.field_27410 = modelPart.method_32086("right_front_leg");
        this.field_27409 = modelPart.method_32086("left_front_leg");
    }

    public static class_5607 method_31991(class_5605 arg) {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, arg), class_5603.method_32090(0.0f, 6.0f, 0.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(16, 16).method_32098(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, arg), class_5603.method_32090(0.0f, 6.0f, 0.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32098(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, arg);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-2.0f, 18.0f, 4.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(2.0f, 18.0f, 4.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-2.0f, 18.0f, -4.0f));
        lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(2.0f, 18.0f, -4.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27406;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.field_27407.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.field_27408.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_27409.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_27410.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}


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
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity>
extends class_5597<T> {
    private final ModelPart field_27532;
    private final ModelPart field_27533;
    private final ModelPart field_27534;
    private final ModelPart field_27535;
    private final ModelPart field_27536;
    private final ModelPart field_27537;

    public WitherEntityModel(ModelPart modelPart) {
        this.field_27532 = modelPart;
        this.field_27536 = modelPart.method_32086("ribcage");
        this.field_27537 = modelPart.method_32086("tail");
        this.field_27533 = modelPart.method_32086("center_head");
        this.field_27534 = modelPart.method_32086("right_head");
        this.field_27535 = modelPart.method_32086("left_head");
    }

    public static class_5607 method_32067(class_5605 arg) {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("shoulders", class_5606.method_32108().method_32101(0, 16).method_32098(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, arg), class_5603.field_27701);
        float f = 0.20420352f;
        lv2.method_32117("ribcage", class_5606.method_32108().method_32101(0, 22).method_32098(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, arg).method_32101(24, 22).method_32098(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, arg).method_32101(24, 22).method_32098(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, arg).method_32101(24, 22).method_32098(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, arg), class_5603.method_32091(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        lv2.method_32117("tail", class_5606.method_32108().method_32101(12, 22).method_32098(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, arg), class_5603.method_32091(-2.0f, 6.9f + MathHelper.cos(0.20420352f) * 10.0f, -0.5f + MathHelper.sin(0.20420352f) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        lv2.method_32117("center_head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, arg), class_5603.field_27701);
        class_5606 lv3 = class_5606.method_32108().method_32101(32, 0).method_32098(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, arg);
        lv2.method_32117("right_head", lv3, class_5603.method_32090(-8.0f, 4.0f, 0.0f));
        lv2.method_32117("left_head", lv3, class_5603.method_32090(10.0f, 4.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27532;
    }

    @Override
    public void setAngles(T witherEntity, float f, float g, float h, float i, float j) {
        float k = MathHelper.cos(h * 0.1f);
        this.field_27536.pitch = (0.065f + 0.05f * k) * (float)Math.PI;
        this.field_27537.setPivot(-2.0f, 6.9f + MathHelper.cos(this.field_27536.pitch) * 10.0f, -0.5f + MathHelper.sin(this.field_27536.pitch) * 10.0f);
        this.field_27537.pitch = (0.265f + 0.1f * k) * (float)Math.PI;
        this.field_27533.yaw = i * ((float)Math.PI / 180);
        this.field_27533.pitch = j * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T witherEntity, float f, float g, float h) {
        WitherEntityModel.method_32066(witherEntity, this.field_27534, 0);
        WitherEntityModel.method_32066(witherEntity, this.field_27535, 1);
    }

    private static <T extends WitherEntity> void method_32066(T witherEntity, ModelPart modelPart, int i) {
        modelPart.yaw = (witherEntity.getHeadYaw(i) - witherEntity.bodyYaw) * ((float)Math.PI / 180);
        modelPart.pitch = witherEntity.getHeadPitch(i) * ((float)Math.PI / 180);
    }
}


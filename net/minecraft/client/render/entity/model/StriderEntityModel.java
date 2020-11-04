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
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity>
extends class_5597<T> {
    private final ModelPart field_27514;
    private final ModelPart field_23353;
    private final ModelPart field_23354;
    private final ModelPart field_23355;
    private final ModelPart field_27515;
    private final ModelPart field_27516;
    private final ModelPart field_27517;
    private final ModelPart field_27518;
    private final ModelPart field_27519;
    private final ModelPart field_27520;

    public StriderEntityModel(ModelPart modelPart) {
        this.field_27514 = modelPart;
        this.field_23353 = modelPart.method_32086("right_leg");
        this.field_23354 = modelPart.method_32086("left_leg");
        this.field_23355 = modelPart.method_32086("body");
        this.field_27515 = this.field_23355.method_32086("right_bottom_bristle");
        this.field_27516 = this.field_23355.method_32086("right_middle_bristle");
        this.field_27517 = this.field_23355.method_32086("right_top_bristle");
        this.field_27518 = this.field_23355.method_32086("left_top_bristle");
        this.field_27519 = this.field_23355.method_32086("left_middle_bristle");
        this.field_27520 = this.field_23355.method_32086("left_bottom_bristle");
    }

    public static class_5607 method_32058() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(0, 32).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), class_5603.method_32090(-4.0f, 8.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(0, 55).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), class_5603.method_32090(4.0f, 8.0f, 0.0f));
        class_5610 lv3 = lv2.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f), class_5603.method_32090(0.0f, 1.0f, 0.0f));
        lv3.method_32117("right_bottom_bristle", class_5606.method_32108().method_32101(16, 65).method_32100(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), class_5603.method_32091(-8.0f, 4.0f, -8.0f, 0.0f, 0.0f, -1.2217305f));
        lv3.method_32117("right_middle_bristle", class_5606.method_32108().method_32101(16, 49).method_32100(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), class_5603.method_32091(-8.0f, -1.0f, -8.0f, 0.0f, 0.0f, -1.134464f));
        lv3.method_32117("right_top_bristle", class_5606.method_32108().method_32101(16, 33).method_32100(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), class_5603.method_32091(-8.0f, -5.0f, -8.0f, 0.0f, 0.0f, -0.87266463f));
        lv3.method_32117("left_top_bristle", class_5606.method_32108().method_32101(16, 33).method_32097(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), class_5603.method_32091(8.0f, -6.0f, -8.0f, 0.0f, 0.0f, 0.87266463f));
        lv3.method_32117("left_middle_bristle", class_5606.method_32108().method_32101(16, 49).method_32097(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), class_5603.method_32091(8.0f, -2.0f, -8.0f, 0.0f, 0.0f, 1.134464f));
        lv3.method_32117("left_bottom_bristle", class_5606.method_32108().method_32101(16, 65).method_32097(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), class_5603.method_32091(8.0f, 3.0f, -8.0f, 0.0f, 0.0f, 1.2217305f));
        return class_5607.method_32110(lv, 64, 128);
    }

    @Override
    public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
        g = Math.min(0.25f, g);
        if (!striderEntity.hasPassengers()) {
            this.field_23355.pitch = j * ((float)Math.PI / 180);
            this.field_23355.yaw = i * ((float)Math.PI / 180);
        } else {
            this.field_23355.pitch = 0.0f;
            this.field_23355.yaw = 0.0f;
        }
        float k = 1.5f;
        this.field_23355.roll = 0.1f * MathHelper.sin(f * 1.5f) * 4.0f * g;
        this.field_23355.pivotY = 2.0f;
        this.field_23355.pivotY -= 2.0f * MathHelper.cos(f * 1.5f) * 2.0f * g;
        this.field_23354.pitch = MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_23353.pitch = MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23354.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f) * g;
        this.field_23353.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f + (float)Math.PI) * g;
        this.field_23354.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23353.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_27515.roll = -1.2217305f;
        this.field_27516.roll = -1.134464f;
        this.field_27517.roll = -0.87266463f;
        this.field_27518.roll = 0.87266463f;
        this.field_27519.roll = 1.134464f;
        this.field_27520.roll = 1.2217305f;
        float l = MathHelper.cos(f * 1.5f + (float)Math.PI) * g;
        this.field_27515.roll += l * 1.3f;
        this.field_27516.roll += l * 1.2f;
        this.field_27517.roll += l * 0.6f;
        this.field_27518.roll += l * 0.6f;
        this.field_27519.roll += l * 1.2f;
        this.field_27520.roll += l * 1.3f;
        float m = 1.0f;
        float n = 1.0f;
        this.field_27515.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
        this.field_27516.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_27517.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_27518.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_27519.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_27520.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27514;
    }
}


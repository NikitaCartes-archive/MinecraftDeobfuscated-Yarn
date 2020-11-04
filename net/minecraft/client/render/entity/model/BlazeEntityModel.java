/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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
public class BlazeEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27394;
    private final ModelPart[] rods;
    private final ModelPart field_27395;

    public BlazeEntityModel(ModelPart modelPart) {
        this.field_27394 = modelPart;
        this.field_27395 = modelPart.method_32086("head");
        this.rods = new ModelPart[12];
        Arrays.setAll(this.rods, i -> modelPart.method_32086(BlazeEntityModel.method_31983(i)));
    }

    private static String method_31983(int i) {
        return "part" + i;
    }

    public static class_5607 method_31982() {
        float j;
        float h;
        float g;
        int i;
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), class_5603.field_27701);
        float f = 0.0f;
        class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32097(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        for (i = 0; i < 4; ++i) {
            g = MathHelper.cos(f) * 9.0f;
            h = -2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 9.0f;
            lv2.method_32117(BlazeEntityModel.method_31983(i), lv3, class_5603.method_32090(g, h, j));
            f += 1.5707964f;
        }
        f = 0.7853982f;
        for (i = 4; i < 8; ++i) {
            g = MathHelper.cos(f) * 7.0f;
            h = 2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 7.0f;
            lv2.method_32117(BlazeEntityModel.method_31983(i), lv3, class_5603.method_32090(g, h, j));
            f += 1.5707964f;
        }
        f = 0.47123894f;
        for (i = 8; i < 12; ++i) {
            g = MathHelper.cos(f) * 5.0f;
            h = 11.0f + MathHelper.cos((float)i * 1.5f * 0.5f);
            j = MathHelper.sin(f) * 5.0f;
            lv2.method_32117(BlazeEntityModel.method_31983(i), lv3, class_5603.method_32090(g, h, j));
            f += 1.5707964f;
        }
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27394;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        int i;
        float f = animationProgress * (float)Math.PI * -0.1f;
        for (i = 0; i < 4; ++i) {
            this.rods[i].pivotY = -2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 9.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 9.0f;
            f += 1.5707964f;
        }
        f = 0.7853982f + animationProgress * (float)Math.PI * 0.03f;
        for (i = 4; i < 8; ++i) {
            this.rods[i].pivotY = 2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 7.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 7.0f;
            f += 1.5707964f;
        }
        f = 0.47123894f + animationProgress * (float)Math.PI * -0.05f;
        for (i = 8; i < 12; ++i) {
            this.rods[i].pivotY = 11.0f + MathHelper.cos(((float)i * 1.5f + animationProgress) * 0.5f);
            this.rods[i].pivotX = MathHelper.cos(f) * 5.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 5.0f;
            f += 1.5707964f;
        }
        this.field_27395.yaw = headYaw * ((float)Math.PI / 180);
        this.field_27395.pitch = headPitch * ((float)Math.PI / 180);
    }
}


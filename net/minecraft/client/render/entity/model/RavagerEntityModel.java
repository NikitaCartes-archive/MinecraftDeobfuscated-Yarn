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
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends class_5597<RavagerEntity> {
    private final ModelPart field_27489;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart field_27490;
    private final ModelPart field_27491;
    private final ModelPart field_27492;
    private final ModelPart field_27493;
    private final ModelPart neck;

    public RavagerEntityModel(ModelPart modelPart) {
        this.field_27489 = modelPart;
        this.neck = modelPart.method_32086("neck");
        this.head = this.neck.method_32086("head");
        this.jaw = this.head.method_32086("mouth");
        this.field_27490 = modelPart.method_32086("right_hind_leg");
        this.field_27491 = modelPart.method_32086("left_hind_leg");
        this.field_27492 = modelPart.method_32086("right_front_leg");
        this.field_27493 = modelPart.method_32086("left_front_leg");
    }

    public static class_5607 method_32035() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 16;
        class_5610 lv3 = lv2.method_32117("neck", class_5606.method_32108().method_32101(68, 73).method_32097(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f), class_5603.method_32090(0.0f, -7.0f, 5.5f));
        class_5610 lv4 = lv3.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).method_32101(0, 0).method_32097(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), class_5603.method_32090(0.0f, 16.0f, -17.0f));
        lv4.method_32117("right_horn", class_5606.method_32108().method_32101(74, 55).method_32097(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), class_5603.method_32091(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        lv4.method_32117("left_horn", class_5606.method_32108().method_32101(74, 55).method_32096().method_32097(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), class_5603.method_32091(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        lv4.method_32117("mouth", class_5606.method_32108().method_32101(0, 36).method_32097(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), class_5603.method_32090(0.0f, -2.0f, 2.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 55).method_32097(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f).method_32101(0, 91).method_32097(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f), class_5603.method_32091(0.0f, 1.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.method_32117("right_hind_leg", class_5606.method_32108().method_32101(96, 0).method_32097(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), class_5603.method_32090(-8.0f, -13.0f, 18.0f));
        lv2.method_32117("left_hind_leg", class_5606.method_32108().method_32101(96, 0).method_32096().method_32097(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), class_5603.method_32090(8.0f, -13.0f, 18.0f));
        lv2.method_32117("right_front_leg", class_5606.method_32108().method_32101(64, 0).method_32097(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), class_5603.method_32090(-8.0f, -13.0f, -5.0f));
        lv2.method_32117("left_front_leg", class_5606.method_32108().method_32101(64, 0).method_32096().method_32097(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), class_5603.method_32090(8.0f, -13.0f, -5.0f));
        return class_5607.method_32110(lv, 128, 128);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27489;
    }

    @Override
    public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        float k = 0.4f * g;
        this.field_27490.pitch = MathHelper.cos(f * 0.6662f) * k;
        this.field_27491.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.field_27492.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.field_27493.pitch = MathHelper.cos(f * 0.6662f) * k;
    }

    @Override
    public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
        super.animateModel(ravagerEntity, f, g, h);
        int i = ravagerEntity.getStunTick();
        int j = ravagerEntity.getRoarTick();
        int k = 20;
        int l = ravagerEntity.getAttackTick();
        int m = 10;
        if (l > 0) {
            float n = MathHelper.method_24504((float)l - h, 10.0f);
            float o = (1.0f + n) * 0.5f;
            float p = o * o * o * 12.0f;
            float q = p * MathHelper.sin(this.neck.pitch);
            this.neck.pivotZ = -6.5f + p;
            this.neck.pivotY = -7.0f - q;
            float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
            this.jaw.pitch = 1.5707964f * r;
            this.jaw.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
        } else {
            float n = -1.0f;
            float o = -1.0f * MathHelper.sin(this.neck.pitch);
            this.neck.pivotX = 0.0f;
            this.neck.pivotY = -7.0f - o;
            this.neck.pivotZ = 5.5f;
            boolean bl = i > 0;
            this.neck.pitch = bl ? 0.21991149f : 0.0f;
            this.jaw.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)i / 40.0;
                this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (j > 0) {
                float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
                this.jaw.pitch = 1.5707964f * q;
            }
        }
    }
}


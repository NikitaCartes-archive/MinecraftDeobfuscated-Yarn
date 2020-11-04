/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity>
extends QuadrupedEntityModel<T> {
    public PolarBearEntityModel(ModelPart modelPart) {
        super(modelPart, true, 16.0f, 4.0f, 2.25f, 2.0f, 24);
    }

    public static class_5607 method_32029() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-3.5f, -3.0f, -3.0f, 7.0f, 7.0f, 7.0f).method_32101(0, 44).method_32102("mouth", -2.5f, 1.0f, -6.0f, 5.0f, 3.0f, 3.0f).method_32101(26, 0).method_32102("right_ear", -4.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f).method_32101(26, 0).method_32096().method_32102("left_ear", 2.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f), class_5603.method_32090(0.0f, 10.0f, -16.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 19).method_32097(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f).method_32101(39, 0).method_32097(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f), class_5603.method_32091(-2.0f, 9.0f, 12.0f, 1.5707964f, 0.0f, 0.0f));
        int i = 10;
        class_5606 lv3 = class_5606.method_32108().method_32101(50, 22).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-4.5f, 14.0f, 6.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(4.5f, 14.0f, 6.0f));
        class_5606 lv4 = class_5606.method_32108().method_32101(50, 40).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f);
        lv2.method_32117("right_front_leg", lv4, class_5603.method_32090(-3.5f, 14.0f, -8.0f));
        lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(3.5f, 14.0f, -8.0f));
        return class_5607.method_32110(lv, 128, 64);
    }

    @Override
    public void setAngles(T polarBearEntity, float f, float g, float h, float i, float j) {
        super.setAngles(polarBearEntity, f, g, h, i, j);
        float k = h - (float)((PolarBearEntity)polarBearEntity).age;
        float l = ((PolarBearEntity)polarBearEntity).getWarningAnimationProgress(k);
        l *= l;
        float m = 1.0f - l;
        this.torso.pitch = 1.5707964f - l * (float)Math.PI * 0.35f;
        this.torso.pivotY = 9.0f * m + 11.0f * l;
        this.field_27478.pivotY = 14.0f * m - 6.0f * l;
        this.field_27478.pivotZ = -8.0f * m - 4.0f * l;
        this.field_27478.pitch -= l * (float)Math.PI * 0.45f;
        this.field_27479.pivotY = this.field_27478.pivotY;
        this.field_27479.pivotZ = this.field_27478.pivotZ;
        this.field_27479.pitch -= l * (float)Math.PI * 0.45f;
        if (this.child) {
            this.head.pivotY = 10.0f * m - 9.0f * l;
            this.head.pivotZ = -16.0f * m - 7.0f * l;
        } else {
            this.head.pivotY = 10.0f * m - 14.0f * l;
            this.head.pivotZ = -16.0f * m - 3.0f * l;
        }
        this.head.pitch += l * (float)Math.PI * 0.15f;
    }
}


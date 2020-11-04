/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(value=EnvType.CLIENT)
public class SheepWoolEntityModel<T extends SheepEntity>
extends QuadrupedEntityModel<T> {
    private float field_3541;

    public SheepWoolEntityModel(ModelPart modelPart) {
        super(modelPart, false, 8.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static class_5607 method_32037() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-3.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, new class_5605(0.6f)), class_5603.method_32090(0.0f, 6.0f, -8.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(28, 8).method_32098(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f, new class_5605(1.75f)), class_5603.method_32091(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32098(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, new class_5605(0.5f));
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-3.0f, 12.0f, 7.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(3.0f, 12.0f, 7.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-3.0f, 12.0f, -5.0f));
        lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(3.0f, 12.0f, -5.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void animateModel(T sheepEntity, float f, float g, float h) {
        super.animateModel(sheepEntity, f, g, h);
        this.head.pivotY = 6.0f + ((SheepEntity)sheepEntity).getNeckAngle(h) * 9.0f;
        this.field_3541 = ((SheepEntity)sheepEntity).getHeadAngle(h);
    }

    @Override
    public void setAngles(T sheepEntity, float f, float g, float h, float i, float j) {
        super.setAngles(sheepEntity, f, g, h, i, j);
        this.head.pitch = this.field_3541;
    }
}


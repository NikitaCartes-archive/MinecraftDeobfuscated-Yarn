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
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class CowEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public CowEntityModel(ModelPart modelPart) {
        super(modelPart, false, 10.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static class_5607 method_31990() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 12;
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).method_32101(22, 0).method_32102("right_horn", -5.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f).method_32101(22, 0).method_32102("left_horn", 4.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f), class_5603.method_32090(0.0f, 4.0f, -8.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(18, 4).method_32097(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).method_32101(52, 0).method_32097(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), class_5603.method_32091(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(0, 16).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-4.0f, 12.0f, 7.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(4.0f, 12.0f, 7.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-4.0f, 12.0f, -6.0f));
        lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(4.0f, 12.0f, -6.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    public ModelPart getHead() {
        return this.head;
    }
}


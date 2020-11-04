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
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class PigEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public PigEntityModel(ModelPart modelPart) {
        super(modelPart, false, 4.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static class_5607 method_32025(class_5605 arg) {
        class_5609 lv = QuadrupedEntityModel.method_32033(6, arg);
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, arg).method_32101(16, 16).method_32098(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 1.0f, arg), class_5603.method_32090(0.0f, 12.0f, -6.0f));
        return class_5607.method_32110(lv, 64, 32);
    }
}


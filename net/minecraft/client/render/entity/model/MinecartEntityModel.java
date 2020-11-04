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

@Environment(value=EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27452;
    private final ModelPart field_27453;

    public MinecartEntityModel(ModelPart modelPart) {
        this.field_27452 = modelPart;
        this.field_27453 = modelPart.method_32086("contents");
    }

    public static class_5607 method_32020() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        lv2.method_32117("bottom", class_5606.method_32108().method_32101(0, 10).method_32097(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f), class_5603.method_32091(0.0f, 4.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.method_32117("front", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), class_5603.method_32091(-9.0f, 4.0f, 0.0f, 0.0f, 4.712389f, 0.0f));
        lv2.method_32117("back", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), class_5603.method_32091(9.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        lv2.method_32117("left", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), class_5603.method_32091(0.0f, 4.0f, -7.0f, 0.0f, (float)Math.PI, 0.0f));
        lv2.method_32117("right", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), class_5603.method_32090(0.0f, 4.0f, 7.0f));
        lv2.method_32117("contents", class_5606.method_32108().method_32101(44, 10).method_32097(-9.0f, -7.0f, -1.0f, 18.0f, 14.0f, 1.0f), class_5603.method_32091(0.0f, 4.0f, 0.0f, -1.5707964f, 0.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.field_27453.pivotY = 4.0f - animationProgress;
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27452;
    }
}


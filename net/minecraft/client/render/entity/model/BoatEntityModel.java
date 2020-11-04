/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends CompositeEntityModel<BoatEntity> {
    private final ModelPart field_27396;
    private final ModelPart field_27397;
    private final ModelPart bottom;
    private final ImmutableList<ModelPart> parts;

    public BoatEntityModel(ModelPart modelPart) {
        this.field_27396 = modelPart.method_32086("left_paddle");
        this.field_27397 = modelPart.method_32086("right_paddle");
        this.bottom = modelPart.method_32086("water_patch");
        this.parts = ImmutableList.of(modelPart.method_32086("bottom"), modelPart.method_32086("back"), modelPart.method_32086("front"), modelPart.method_32086("right"), modelPart.method_32086("left"), this.field_27396, this.field_27397);
    }

    public static class_5607 method_31985() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        lv2.method_32117("bottom", class_5606.method_32108().method_32101(0, 0).method_32097(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), class_5603.method_32091(0.0f, 3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.method_32117("back", class_5606.method_32108().method_32101(0, 19).method_32097(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), class_5603.method_32091(-15.0f, 4.0f, 4.0f, 0.0f, 4.712389f, 0.0f));
        lv2.method_32117("front", class_5606.method_32108().method_32101(0, 27).method_32097(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), class_5603.method_32091(15.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        lv2.method_32117("right", class_5606.method_32108().method_32101(0, 35).method_32097(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), class_5603.method_32091(0.0f, 4.0f, -9.0f, 0.0f, (float)Math.PI, 0.0f));
        lv2.method_32117("left", class_5606.method_32108().method_32101(0, 43).method_32097(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), class_5603.method_32090(0.0f, 4.0f, 9.0f));
        int n = 20;
        int o = 7;
        int p = 6;
        float f = -5.0f;
        lv2.method_32117("left_paddle", class_5606.method_32108().method_32101(62, 0).method_32097(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).method_32097(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), class_5603.method_32091(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        lv2.method_32117("right_paddle", class_5606.method_32108().method_32101(62, 20).method_32097(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).method_32097(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), class_5603.method_32091(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
        lv2.method_32117("water_patch", class_5606.method_32108().method_32101(0, 0).method_32097(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), class_5603.method_32091(0.0f, -3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        return class_5607.method_32110(lv, 128, 64);
    }

    @Override
    public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
        BoatEntityModel.setPaddleAngle(boatEntity, 0, this.field_27396, f);
        BoatEntityModel.setPaddleAngle(boatEntity, 1, this.field_27397, f);
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    public ModelPart getBottom() {
        return this.bottom;
    }

    private static void setPaddleAngle(BoatEntity boatEntity, int i, ModelPart modelPart, float angle) {
        float f = boatEntity.interpolatePaddlePhase(i, angle);
        modelPart.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        modelPart.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (i == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}


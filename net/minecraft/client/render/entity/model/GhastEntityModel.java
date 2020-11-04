/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Random;
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
public class GhastEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27419;
    private final ModelPart[] tentacles = new ModelPart[9];

    public GhastEntityModel(ModelPart modelPart) {
        this.field_27419 = modelPart;
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = modelPart.method_32086(GhastEntityModel.method_32001(i));
        }
    }

    private static String method_32001(int i) {
        return "tentacle" + i;
    }

    public static class_5607 method_32000() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32097(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), class_5603.method_32090(0.0f, 17.6f, 0.0f));
        Random random = new Random(1660L);
        for (int i = 0; i < 9; ++i) {
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int j = random.nextInt(7) + 8;
            lv2.method_32117(GhastEntityModel.method_32001(i), class_5606.method_32108().method_32101(0, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, j, 2.0f), class_5603.method_32090(f, 24.6f, g));
        }
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].pitch = 0.2f * MathHelper.sin(animationProgress * 0.3f + (float)i) + 0.4f;
        }
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27419;
    }
}


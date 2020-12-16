/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class ModelUtil {
    public static float interpolateAngle(float angle1, float angle2, float progress) {
        float f;
        for (f = angle2 - angle1; f < (float)(-Math.PI); f += (float)Math.PI * 2) {
        }
        while (f >= (float)Math.PI) {
            f -= (float)Math.PI * 2;
        }
        return angle1 + progress * f;
    }

    public static void method_33300(ModelPart modelPart, float f, float g, float h) {
        modelPart.pitch = f;
        modelPart.yaw = g;
        modelPart.roll = h;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public interface ModelWithArms {
    public void setArmAngle(float var1, Arm var2, MatrixStack var3);
}


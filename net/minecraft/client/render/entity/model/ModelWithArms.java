/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.AbsoluteHand;

@Environment(value=EnvType.CLIENT)
public interface ModelWithArms {
    public void setArmAngle(float var1, AbsoluteHand var2);
}


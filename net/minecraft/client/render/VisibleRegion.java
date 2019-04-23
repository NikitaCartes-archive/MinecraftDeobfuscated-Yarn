/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BoundingBox;

@Environment(value=EnvType.CLIENT)
public interface VisibleRegion {
    public boolean intersects(BoundingBox var1);

    public void setOrigin(double var1, double var3, double var5);
}


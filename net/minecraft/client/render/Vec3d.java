/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Vec3d {
    public double x;
    public double y;
    public double z;

    public Vec3d(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;

public abstract class HitResult {
    protected final Vec3d pos;

    protected HitResult(Vec3d vec3d) {
        this.pos = vec3d;
    }

    public abstract Type getType();

    public Vec3d getPos() {
        return this.pos;
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;

    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EntityDimensions {
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntityDimensions(float width, float height, boolean fixed) {
        this.width = width;
        this.height = height;
        this.fixed = fixed;
    }

    public Box getBoxAt(Vec3d pos) {
        return this.getBoxAt(pos.x, pos.y, pos.z);
    }

    public Box getBoxAt(double x, double y, double z) {
        float f = this.width / 2.0f;
        float g = this.height;
        return new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f);
    }

    public EntityDimensions scaled(float ratio) {
        return this.scaled(ratio, ratio);
    }

    public EntityDimensions scaled(float widthRatio, float heightRatio) {
        if (this.fixed || widthRatio == 1.0f && heightRatio == 1.0f) {
            return this;
        }
        return EntityDimensions.changing(this.width * widthRatio, this.height * heightRatio);
    }

    public static EntityDimensions changing(float width, float height) {
        return new EntityDimensions(width, height, false);
    }

    public static EntityDimensions fixed(float width, float height) {
        return new EntityDimensions(width, height, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}


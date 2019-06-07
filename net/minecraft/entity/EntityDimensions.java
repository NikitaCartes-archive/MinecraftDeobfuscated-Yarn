/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

public class EntityDimensions {
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntityDimensions(float f, float g, boolean bl) {
        this.width = f;
        this.height = g;
        this.fixed = bl;
    }

    public EntityDimensions scaled(float f) {
        return this.scaled(f, f);
    }

    public EntityDimensions scaled(float f, float g) {
        if (this.fixed || f == 1.0f && g == 1.0f) {
            return this;
        }
        return EntityDimensions.changing(this.width * f, this.height * g);
    }

    public static EntityDimensions changing(float f, float g) {
        return new EntityDimensions(f, g, false);
    }

    public static EntityDimensions fixed(float f, float g) {
        return new EntityDimensions(f, g, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

public class EntitySize {
    public final float width;
    public final float height;
    public final boolean constant;

    public EntitySize(float f, float g, boolean bl) {
        this.width = f;
        this.height = g;
        this.constant = bl;
    }

    public EntitySize scaled(float f) {
        return this.scaled(f, f);
    }

    public EntitySize scaled(float f, float g) {
        if (this.constant || f == 1.0f && g == 1.0f) {
            return this;
        }
        return EntitySize.resizeable(this.width * f, this.height * g);
    }

    public static EntitySize resizeable(float f, float g) {
        return new EntitySize(f, g, false);
    }

    public static EntitySize constant(float f, float g) {
        return new EntitySize(f, g, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.constant;
    }
}


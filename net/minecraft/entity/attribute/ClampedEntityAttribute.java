/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import net.minecraft.entity.attribute.AbstractEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class ClampedEntityAttribute
extends AbstractEntityAttribute {
    private final double minValue;
    private final double maxValue;
    private String name;

    public ClampedEntityAttribute(@Nullable EntityAttribute entityAttribute, String string, double d, double e, double f) {
        super(entityAttribute, string, d);
        this.minValue = e;
        this.maxValue = f;
        if (e > f) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (d < e) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (d > f) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public ClampedEntityAttribute setName(String string) {
        this.name = string;
        return this;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public double clamp(double d) {
        d = MathHelper.clamp(d, this.minValue, this.maxValue);
        return d;
    }
}


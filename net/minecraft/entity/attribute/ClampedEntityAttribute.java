/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.MathHelper;

public class ClampedEntityAttribute
extends EntityAttribute {
    private final double minValue;
    private final double maxValue;

    public ClampedEntityAttribute(String translationKey, double fallback, double min, double max) {
        super(translationKey, fallback);
        this.minValue = min;
        this.maxValue = max;
        if (min > max) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (fallback < min) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (fallback > max) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    @Override
    public double clamp(double value) {
        value = MathHelper.clamp(value, this.minValue, this.maxValue);
        return value;
    }
}


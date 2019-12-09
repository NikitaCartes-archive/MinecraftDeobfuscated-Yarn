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

    public ClampedEntityAttribute(@Nullable EntityAttribute parent, String id, double defaultValue, double minValue, double maxValue) {
        super(parent, id, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (minValue > maxValue) {
            throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
        }
        if (defaultValue < minValue) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        }
        if (defaultValue > maxValue) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }

    public ClampedEntityAttribute setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public double clamp(double value) {
        value = MathHelper.clamp(value, this.minValue, this.maxValue);
        return value;
    }
}


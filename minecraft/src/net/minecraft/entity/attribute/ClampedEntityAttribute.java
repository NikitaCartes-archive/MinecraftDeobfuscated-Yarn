package net.minecraft.entity.attribute;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class ClampedEntityAttribute extends AbstractEntityAttribute {
	private final double minValue;
	private final double maxValue;
	private String name;

	public ClampedEntityAttribute(@Nullable EntityAttribute parent, String id, double defaultValue, double minValue, double maxValue) {
		super(parent, id, defaultValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
		if (minValue > maxValue) {
			throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
		} else if (defaultValue < minValue) {
			throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
		} else if (defaultValue > maxValue) {
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
		return MathHelper.clamp(value, this.minValue, this.maxValue);
	}
}

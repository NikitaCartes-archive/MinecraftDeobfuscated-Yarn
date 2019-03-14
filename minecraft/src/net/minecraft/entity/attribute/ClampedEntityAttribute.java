package net.minecraft.entity.attribute;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class ClampedEntityAttribute extends AbstractEntityAttribute {
	private final double minValue;
	private final double maxValue;
	private String name;

	public ClampedEntityAttribute(@Nullable EntityAttribute entityAttribute, String string, double d, double e, double f) {
		super(entityAttribute, string, d);
		this.minValue = e;
		this.maxValue = f;
		if (e > f) {
			throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
		} else if (d < e) {
			throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
		} else if (d > f) {
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
		return MathHelper.clamp(d, this.minValue, this.maxValue);
	}
}

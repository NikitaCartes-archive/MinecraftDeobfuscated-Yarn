package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public interface EntityAttribute {
	String getId();

	double clamp(double value);

	double getDefaultValue();

	boolean isTracked();

	@Nullable
	EntityAttribute getParent();
}

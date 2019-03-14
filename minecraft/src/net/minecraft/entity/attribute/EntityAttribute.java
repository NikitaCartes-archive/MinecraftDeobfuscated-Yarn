package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public interface EntityAttribute {
	String getId();

	double clamp(double d);

	double getDefaultValue();

	boolean isTracked();

	@Nullable
	EntityAttribute getParent();
}

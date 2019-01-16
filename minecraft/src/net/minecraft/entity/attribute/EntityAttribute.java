package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public interface EntityAttribute {
	String getId();

	double method_6165(double d);

	double getDefaultValue();

	boolean method_6168();

	@Nullable
	EntityAttribute getParent();
}

package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface EntityAttributeInstance {
	EntityAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double d);

	Collection<EntityAttributeModifier> method_6193(EntityAttributeModifier.Operation operation);

	Collection<EntityAttributeModifier> getModifiers();

	boolean method_6196(EntityAttributeModifier entityAttributeModifier);

	@Nullable
	EntityAttributeModifier method_6199(UUID uUID);

	void method_6197(EntityAttributeModifier entityAttributeModifier);

	void method_6202(EntityAttributeModifier entityAttributeModifier);

	void removeModifier(UUID uUID);

	@Environment(EnvType.CLIENT)
	void clearModifiers();

	double getValue();
}

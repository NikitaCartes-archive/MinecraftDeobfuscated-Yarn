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

	Collection<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation);

	Collection<EntityAttributeModifier> getModifiers();

	boolean hasModifier(EntityAttributeModifier entityAttributeModifier);

	@Nullable
	EntityAttributeModifier getModifier(UUID uUID);

	void addModifier(EntityAttributeModifier entityAttributeModifier);

	void removeModifier(EntityAttributeModifier entityAttributeModifier);

	void removeModifier(UUID uUID);

	@Environment(EnvType.CLIENT)
	void clearModifiers();

	double getValue();
}

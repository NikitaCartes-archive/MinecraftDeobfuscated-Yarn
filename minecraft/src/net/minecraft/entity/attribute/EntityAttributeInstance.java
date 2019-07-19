package net.minecraft.entity.attribute;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface EntityAttributeInstance {
	EntityAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double baseValue);

	Collection<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation);

	Collection<EntityAttributeModifier> getModifiers();

	boolean hasModifier(EntityAttributeModifier modifier);

	@Nullable
	EntityAttributeModifier getModifier(UUID uuid);

	void addModifier(EntityAttributeModifier modifier);

	void removeModifier(EntityAttributeModifier modifier);

	void removeModifier(UUID uuid);

	@Environment(EnvType.CLIENT)
	void clearModifiers();

	double getValue();
}

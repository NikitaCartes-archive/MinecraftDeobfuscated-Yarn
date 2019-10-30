package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface EntityAttributeInstance {
	EntityAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double baseValue);

	Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation);

	Set<EntityAttributeModifier> getModifiers();

	boolean hasModifier(EntityAttributeModifier modifier);

	@Nullable
	EntityAttributeModifier getModifier(UUID uuid);

	void addModifier(EntityAttributeModifier modifier);

	void removeModifier(EntityAttributeModifier modifier);

	void removeModifier(UUID uuid);

	@Environment(EnvType.CLIENT)
	void clearModifiers();

	double getValue();

	@Environment(EnvType.CLIENT)
	default void method_22323(EntityAttributeInstance entityAttributeInstance) {
		this.setBaseValue(entityAttributeInstance.getBaseValue());
		Set<EntityAttributeModifier> set = entityAttributeInstance.getModifiers();
		Set<EntityAttributeModifier> set2 = this.getModifiers();
		ImmutableSet<EntityAttributeModifier> immutableSet = ImmutableSet.copyOf(Sets.difference(set, set2));
		ImmutableSet<EntityAttributeModifier> immutableSet2 = ImmutableSet.copyOf(Sets.difference(set2, set));
		immutableSet.forEach(this::addModifier);
		immutableSet2.forEach(this::removeModifier);
	}
}

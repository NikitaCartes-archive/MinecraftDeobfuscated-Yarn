package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.registry.entry.RegistryEntry;

public class DefaultAttributeContainer {
	private final Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances;

	DefaultAttributeContainer(Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances) {
		this.instances = instances;
	}

	private EntityAttributeInstance require(RegistryEntry<EntityAttribute> attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
		if (entityAttributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + attribute.getIdAsString());
		} else {
			return entityAttributeInstance;
		}
	}

	public double getValue(RegistryEntry<EntityAttribute> attribute) {
		return this.require(attribute).getValue();
	}

	public double getBaseValue(RegistryEntry<EntityAttribute> attribute) {
		return this.require(attribute).getBaseValue();
	}

	public double getModifierValue(RegistryEntry<EntityAttribute> attribute, UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.require(attribute).getModifier(uuid);
		if (entityAttributeModifier == null) {
			throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + attribute.getIdAsString());
		} else {
			return entityAttributeModifier.value();
		}
	}

	@Nullable
	public EntityAttributeInstance createOverride(Consumer<EntityAttributeInstance> updateCallback, RegistryEntry<EntityAttribute> attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
		if (entityAttributeInstance == null) {
			return null;
		} else {
			EntityAttributeInstance entityAttributeInstance2 = new EntityAttributeInstance(attribute, updateCallback);
			entityAttributeInstance2.setFrom(entityAttributeInstance);
			return entityAttributeInstance2;
		}
	}

	public static DefaultAttributeContainer.Builder builder() {
		return new DefaultAttributeContainer.Builder();
	}

	public boolean has(RegistryEntry<EntityAttribute> attribute) {
		return this.instances.containsKey(attribute);
	}

	public boolean hasModifier(RegistryEntry<EntityAttribute> attribute, UUID uuid) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
		return entityAttributeInstance != null && entityAttributeInstance.getModifier(uuid) != null;
	}

	public static class Builder {
		private final ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances = ImmutableMap.builder();
		private boolean unmodifiable;

		private EntityAttributeInstance checkedAdd(RegistryEntry<EntityAttribute> attribute) {
			EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, attributex -> {
				if (this.unmodifiable) {
					throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + attribute.getIdAsString());
				}
			});
			this.instances.put(attribute, entityAttributeInstance);
			return entityAttributeInstance;
		}

		public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> attribute) {
			this.checkedAdd(attribute);
			return this;
		}

		public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> attribute, double baseValue) {
			EntityAttributeInstance entityAttributeInstance = this.checkedAdd(attribute);
			entityAttributeInstance.setBaseValue(baseValue);
			return this;
		}

		public DefaultAttributeContainer build() {
			this.unmodifiable = true;
			return new DefaultAttributeContainer(this.instances.buildKeepingLast());
		}
	}
}

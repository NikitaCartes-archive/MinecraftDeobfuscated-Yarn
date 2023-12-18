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

	private EntityAttributeInstance require(RegistryEntry<EntityAttribute> registryEntry) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(registryEntry);
		if (entityAttributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + registryEntry.method_55840());
		} else {
			return entityAttributeInstance;
		}
	}

	public double getValue(RegistryEntry<EntityAttribute> registryEntry) {
		return this.require(registryEntry).getValue();
	}

	public double getBaseValue(RegistryEntry<EntityAttribute> registryEntry) {
		return this.require(registryEntry).getBaseValue();
	}

	public double getModifierValue(RegistryEntry<EntityAttribute> registryEntry, UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.require(registryEntry).getModifier(uuid);
		if (entityAttributeModifier == null) {
			throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + registryEntry.method_55840());
		} else {
			return entityAttributeModifier.getValue();
		}
	}

	@Nullable
	public EntityAttributeInstance createOverride(Consumer<EntityAttributeInstance> updateCallback, RegistryEntry<EntityAttribute> registryEntry) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(registryEntry);
		if (entityAttributeInstance == null) {
			return null;
		} else {
			EntityAttributeInstance entityAttributeInstance2 = new EntityAttributeInstance(registryEntry, updateCallback);
			entityAttributeInstance2.setFrom(entityAttributeInstance);
			return entityAttributeInstance2;
		}
	}

	public static DefaultAttributeContainer.Builder builder() {
		return new DefaultAttributeContainer.Builder();
	}

	public boolean has(RegistryEntry<EntityAttribute> registryEntry) {
		return this.instances.containsKey(registryEntry);
	}

	public boolean hasModifier(RegistryEntry<EntityAttribute> registryEntry, UUID uuid) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(registryEntry);
		return entityAttributeInstance != null && entityAttributeInstance.getModifier(uuid) != null;
	}

	public static class Builder {
		private final ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances = ImmutableMap.builder();
		private boolean unmodifiable;

		private EntityAttributeInstance checkedAdd(RegistryEntry<EntityAttribute> registryEntry) {
			EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(registryEntry, attributex -> {
				if (this.unmodifiable) {
					throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + registryEntry.method_55840());
				}
			});
			this.instances.put(registryEntry, entityAttributeInstance);
			return entityAttributeInstance;
		}

		public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> registryEntry) {
			this.checkedAdd(registryEntry);
			return this;
		}

		public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> registryEntry, double baseValue) {
			EntityAttributeInstance entityAttributeInstance = this.checkedAdd(registryEntry);
			entityAttributeInstance.setBaseValue(baseValue);
			return this;
		}

		public DefaultAttributeContainer build() {
			this.unmodifiable = true;
			return new DefaultAttributeContainer(this.instances.buildKeepingLast());
		}
	}
}

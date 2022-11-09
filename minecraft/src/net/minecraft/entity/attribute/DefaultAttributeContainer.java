package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.registry.Registries;

public class DefaultAttributeContainer {
	private final Map<EntityAttribute, EntityAttributeInstance> instances;

	public DefaultAttributeContainer(Map<EntityAttribute, EntityAttributeInstance> instances) {
		this.instances = ImmutableMap.copyOf(instances);
	}

	private EntityAttributeInstance require(EntityAttribute attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
		if (entityAttributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + Registries.ATTRIBUTE.getId(attribute));
		} else {
			return entityAttributeInstance;
		}
	}

	public double getValue(EntityAttribute attribute) {
		return this.require(attribute).getValue();
	}

	public double getBaseValue(EntityAttribute attribute) {
		return this.require(attribute).getBaseValue();
	}

	public double getModifierValue(EntityAttribute attribute, UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.require(attribute).getModifier(uuid);
		if (entityAttributeModifier == null) {
			throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + Registries.ATTRIBUTE.getId(attribute));
		} else {
			return entityAttributeModifier.getValue();
		}
	}

	@Nullable
	public EntityAttributeInstance createOverride(Consumer<EntityAttributeInstance> updateCallback, EntityAttribute attribute) {
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

	public boolean has(EntityAttribute type) {
		return this.instances.containsKey(type);
	}

	public boolean hasModifier(EntityAttribute type, UUID uuid) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(type);
		return entityAttributeInstance != null && entityAttributeInstance.getModifier(uuid) != null;
	}

	public static class Builder {
		private final Map<EntityAttribute, EntityAttributeInstance> instances = Maps.<EntityAttribute, EntityAttributeInstance>newHashMap();
		private boolean unmodifiable;

		private EntityAttributeInstance checkedAdd(EntityAttribute attribute) {
			EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, attributex -> {
				if (this.unmodifiable) {
					throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registries.ATTRIBUTE.getId(attribute));
				}
			});
			this.instances.put(attribute, entityAttributeInstance);
			return entityAttributeInstance;
		}

		public DefaultAttributeContainer.Builder add(EntityAttribute attribute) {
			this.checkedAdd(attribute);
			return this;
		}

		public DefaultAttributeContainer.Builder add(EntityAttribute attribute, double baseValue) {
			EntityAttributeInstance entityAttributeInstance = this.checkedAdd(attribute);
			entityAttributeInstance.setBaseValue(baseValue);
			return this;
		}

		public DefaultAttributeContainer build() {
			this.unmodifiable = true;
			return new DefaultAttributeContainer(this.instances);
		}
	}
}

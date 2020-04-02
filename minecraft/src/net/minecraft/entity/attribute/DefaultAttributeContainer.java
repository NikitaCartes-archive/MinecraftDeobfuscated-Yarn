package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;

public class DefaultAttributeContainer {
	private final Map<EntityAttribute, EntityAttributeInstance> instances;

	public DefaultAttributeContainer(Map<EntityAttribute, EntityAttributeInstance> instances) {
		this.instances = ImmutableMap.copyOf(instances);
	}

	private EntityAttributeInstance require(EntityAttribute attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
		if (entityAttributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTES.getId(attribute));
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

	public static class Builder {
		private final Map<EntityAttribute, EntityAttributeInstance> instances = Maps.<EntityAttribute, EntityAttributeInstance>newHashMap();
		private boolean unmodifiable;

		private EntityAttributeInstance checkedAdd(EntityAttribute attribute) {
			EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, entityAttributeInstancex -> {
				if (this.unmodifiable) {
					throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTES.getId(attribute));
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

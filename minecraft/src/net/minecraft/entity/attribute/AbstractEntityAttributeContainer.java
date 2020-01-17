package net.minecraft.entity.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.LowercaseMap;

public abstract class AbstractEntityAttributeContainer {
	protected final Map<EntityAttribute, EntityAttributeInstance> instancesByKey = Maps.<EntityAttribute, EntityAttributeInstance>newHashMap();
	protected final Map<String, EntityAttributeInstance> instancesById = new LowercaseMap();
	protected final Multimap<EntityAttribute, EntityAttribute> attributeHierarchy = HashMultimap.create();

	@Nullable
	public EntityAttributeInstance get(EntityAttribute attribute) {
		return (EntityAttributeInstance)this.instancesByKey.get(attribute);
	}

	@Nullable
	public EntityAttributeInstance get(String name) {
		return (EntityAttributeInstance)this.instancesById.get(name);
	}

	public EntityAttributeInstance register(EntityAttribute attribute) {
		if (this.instancesById.containsKey(attribute.getId())) {
			throw new IllegalArgumentException("Attribute is already registered!");
		} else {
			EntityAttributeInstance entityAttributeInstance = this.createInstance(attribute);
			this.instancesById.put(attribute.getId(), entityAttributeInstance);
			this.instancesByKey.put(attribute, entityAttributeInstance);

			for (EntityAttribute entityAttribute = attribute.getParent(); entityAttribute != null; entityAttribute = entityAttribute.getParent()) {
				this.attributeHierarchy.put(entityAttribute, attribute);
			}

			return entityAttributeInstance;
		}
	}

	protected abstract EntityAttributeInstance createInstance(EntityAttribute attribute);

	public Collection<EntityAttributeInstance> values() {
		return this.instancesById.values();
	}

	public void add(EntityAttributeInstance instance) {
	}

	public void removeAll(Multimap<String, EntityAttributeModifier> modifiers) {
		for (Entry<String, EntityAttributeModifier> entry : modifiers.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	public void replaceAll(Multimap<String, EntityAttributeModifier> modifiers) {
		for (Entry<String, EntityAttributeModifier> entry : modifiers.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
				entityAttributeInstance.addModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void copyFrom(AbstractEntityAttributeContainer attributeContainer) {
		this.values().forEach(entityAttributeInstance -> {
			EntityAttributeInstance entityAttributeInstance2 = attributeContainer.get(entityAttributeInstance.getAttribute());
			if (entityAttributeInstance2 != null) {
				entityAttributeInstance.copyFrom(entityAttributeInstance2);
			}
		});
	}
}

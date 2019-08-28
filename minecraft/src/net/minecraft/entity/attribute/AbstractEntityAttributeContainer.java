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
	public EntityAttributeInstance get(EntityAttribute entityAttribute) {
		return (EntityAttributeInstance)this.instancesByKey.get(entityAttribute);
	}

	@Nullable
	public EntityAttributeInstance get(String string) {
		return (EntityAttributeInstance)this.instancesById.get(string);
	}

	public EntityAttributeInstance register(EntityAttribute entityAttribute) {
		if (this.instancesById.containsKey(entityAttribute.getId())) {
			throw new IllegalArgumentException("Attribute is already registered!");
		} else {
			EntityAttributeInstance entityAttributeInstance = this.createInstance(entityAttribute);
			this.instancesById.put(entityAttribute.getId(), entityAttributeInstance);
			this.instancesByKey.put(entityAttribute, entityAttributeInstance);

			for (EntityAttribute entityAttribute2 = entityAttribute.getParent(); entityAttribute2 != null; entityAttribute2 = entityAttribute2.getParent()) {
				this.attributeHierarchy.put(entityAttribute2, entityAttribute);
			}

			return entityAttributeInstance;
		}
	}

	protected abstract EntityAttributeInstance createInstance(EntityAttribute entityAttribute);

	public Collection<EntityAttributeInstance> values() {
		return this.instancesById.values();
	}

	public void add(EntityAttributeInstance entityAttributeInstance) {
	}

	public void removeAll(Multimap<String, EntityAttributeModifier> multimap) {
		for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	public void replaceAll(Multimap<String, EntityAttributeModifier> multimap) {
		for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
				entityAttributeInstance.addModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_22324(AbstractEntityAttributeContainer abstractEntityAttributeContainer) {
		this.values().forEach(entityAttributeInstance -> {
			EntityAttributeInstance entityAttributeInstance2 = abstractEntityAttributeContainer.get(entityAttributeInstance.getAttribute());
			if (entityAttributeInstance2 != null) {
				entityAttributeInstance.method_22323(entityAttributeInstance2);
			}
		});
	}
}

package net.minecraft.entity.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.LowercaseMap;

public abstract class AbstractEntityAttributeContainer {
	protected final Map<EntityAttribute, EntityAttributeInstance> instancesByKey = Maps.<EntityAttribute, EntityAttributeInstance>newHashMap();
	protected final Map<String, EntityAttributeInstance> instancesById = new LowercaseMap();
	protected final Multimap<EntityAttribute, EntityAttribute> field_6336 = HashMultimap.create();

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
				this.field_6336.put(entityAttribute2, entityAttribute);
			}

			return entityAttributeInstance;
		}
	}

	protected abstract EntityAttributeInstance createInstance(EntityAttribute entityAttribute);

	public Collection<EntityAttributeInstance> values() {
		return this.instancesById.values();
	}

	public void method_6211(EntityAttributeInstance entityAttributeInstance) {
	}

	public void method_6209(Multimap<String, EntityAttributeModifier> multimap) {
		for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.method_6202((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	public void method_6210(Multimap<String, EntityAttributeModifier> multimap) {
		for (Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
			EntityAttributeInstance entityAttributeInstance = this.get((String)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.method_6202((EntityAttributeModifier)entry.getValue());
				entityAttributeInstance.method_6197((EntityAttributeModifier)entry.getValue());
			}
		}
	}
}

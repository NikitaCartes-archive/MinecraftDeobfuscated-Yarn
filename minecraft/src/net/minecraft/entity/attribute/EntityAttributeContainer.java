package net.minecraft.entity.attribute;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowercaseMap;

public class EntityAttributeContainer extends AbstractEntityAttributeContainer {
	private final Set<EntityAttributeInstance> trackedAttributes = Sets.<EntityAttributeInstance>newHashSet();
	protected final Map<String, EntityAttributeInstance> instancesByName = new LowercaseMap();

	public EntityAttributeInstanceImpl get(EntityAttribute entityAttribute) {
		return (EntityAttributeInstanceImpl)super.get(entityAttribute);
	}

	public EntityAttributeInstanceImpl get(String string) {
		EntityAttributeInstance entityAttributeInstance = super.get(string);
		if (entityAttributeInstance == null) {
			entityAttributeInstance = (EntityAttributeInstance)this.instancesByName.get(string);
		}

		return (EntityAttributeInstanceImpl)entityAttributeInstance;
	}

	@Override
	public EntityAttributeInstance register(EntityAttribute attribute) {
		EntityAttributeInstance entityAttributeInstance = super.register(attribute);
		if (attribute instanceof ClampedEntityAttribute && ((ClampedEntityAttribute)attribute).getName() != null) {
			this.instancesByName.put(((ClampedEntityAttribute)attribute).getName(), entityAttributeInstance);
		}

		return entityAttributeInstance;
	}

	@Override
	protected EntityAttributeInstance createInstance(EntityAttribute attribute) {
		return new EntityAttributeInstanceImpl(this, attribute);
	}

	@Override
	public void add(EntityAttributeInstance instance) {
		if (instance.getAttribute().isTracked()) {
			this.trackedAttributes.add(instance);
		}

		for (EntityAttribute entityAttribute : this.attributeHierarchy.get(instance.getAttribute())) {
			EntityAttributeInstanceImpl entityAttributeInstanceImpl = this.get(entityAttribute);
			if (entityAttributeInstanceImpl != null) {
				entityAttributeInstanceImpl.invalidateCache();
			}
		}
	}

	public Set<EntityAttributeInstance> getTrackedAttributes() {
		return this.trackedAttributes;
	}

	public Collection<EntityAttributeInstance> buildTrackedAttributesCollection() {
		Set<EntityAttributeInstance> set = Sets.<EntityAttributeInstance>newHashSet();

		for (EntityAttributeInstance entityAttributeInstance : this.values()) {
			if (entityAttributeInstance.getAttribute().isTracked()) {
				set.add(entityAttributeInstance);
			}
		}

		return set;
	}
}

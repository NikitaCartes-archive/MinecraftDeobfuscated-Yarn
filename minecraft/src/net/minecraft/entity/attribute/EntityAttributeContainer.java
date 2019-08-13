package net.minecraft.entity.attribute;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowercaseMap;

public class EntityAttributeContainer extends AbstractEntityAttributeContainer {
	private final Set<EntityAttributeInstance> trackedAttributes = Sets.<EntityAttributeInstance>newHashSet();
	protected final Map<String, EntityAttributeInstance> instancesByName = new LowercaseMap();

	public EntityAttributeInstanceImpl method_6216(EntityAttribute entityAttribute) {
		return (EntityAttributeInstanceImpl)super.get(entityAttribute);
	}

	public EntityAttributeInstanceImpl method_6214(String string) {
		EntityAttributeInstance entityAttributeInstance = super.get(string);
		if (entityAttributeInstance == null) {
			entityAttributeInstance = (EntityAttributeInstance)this.instancesByName.get(string);
		}

		return (EntityAttributeInstanceImpl)entityAttributeInstance;
	}

	@Override
	public EntityAttributeInstance register(EntityAttribute entityAttribute) {
		EntityAttributeInstance entityAttributeInstance = super.register(entityAttribute);
		if (entityAttribute instanceof ClampedEntityAttribute && ((ClampedEntityAttribute)entityAttribute).getName() != null) {
			this.instancesByName.put(((ClampedEntityAttribute)entityAttribute).getName(), entityAttributeInstance);
		}

		return entityAttributeInstance;
	}

	@Override
	protected EntityAttributeInstance createInstance(EntityAttribute entityAttribute) {
		return new EntityAttributeInstanceImpl(this, entityAttribute);
	}

	@Override
	public void add(EntityAttributeInstance entityAttributeInstance) {
		if (entityAttributeInstance.getAttribute().isTracked()) {
			this.trackedAttributes.add(entityAttributeInstance);
		}

		for (EntityAttribute entityAttribute : this.attributeHierarchy.get(entityAttributeInstance.getAttribute())) {
			EntityAttributeInstanceImpl entityAttributeInstanceImpl = this.method_6216(entityAttribute);
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

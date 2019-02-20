package net.minecraft.entity.attribute;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowercaseMap;

public class EntityAttributeContainer extends AbstractEntityAttributeContainer {
	private final Set<EntityAttributeInstance> field_6342 = Sets.<EntityAttributeInstance>newHashSet();
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
	public void method_6211(EntityAttributeInstance entityAttributeInstance) {
		if (entityAttributeInstance.getAttribute().method_6168()) {
			this.field_6342.add(entityAttributeInstance);
		}

		for (EntityAttribute entityAttribute : this.field_6336.get(entityAttributeInstance.getAttribute())) {
			EntityAttributeInstanceImpl entityAttributeInstanceImpl = this.method_6216(entityAttribute);
			if (entityAttributeInstanceImpl != null) {
				entityAttributeInstanceImpl.method_6217();
			}
		}
	}

	public Set<EntityAttributeInstance> method_6215() {
		return this.field_6342;
	}

	public Collection<EntityAttributeInstance> method_6213() {
		Set<EntityAttributeInstance> set = Sets.<EntityAttributeInstance>newHashSet();

		for (EntityAttributeInstance entityAttributeInstance : this.values()) {
			if (entityAttributeInstance.getAttribute().method_6168()) {
				set.add(entityAttributeInstance);
			}
		}

		return set;
	}
}

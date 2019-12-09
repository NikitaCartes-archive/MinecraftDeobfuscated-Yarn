/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeInstanceImpl;
import net.minecraft.util.LowercaseMap;

public class EntityAttributeContainer
extends AbstractEntityAttributeContainer {
    private final Set<EntityAttributeInstance> trackedAttributes = Sets.newHashSet();
    protected final Map<String, EntityAttributeInstance> instancesByName = new LowercaseMap<EntityAttributeInstance>();

    @Override
    public EntityAttributeInstanceImpl get(EntityAttribute entityAttribute) {
        return (EntityAttributeInstanceImpl)super.get(entityAttribute);
    }

    @Override
    public EntityAttributeInstanceImpl get(String string) {
        EntityAttributeInstance entityAttributeInstance = super.get(string);
        if (entityAttributeInstance == null) {
            entityAttributeInstance = this.instancesByName.get(string);
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
            if (entityAttributeInstanceImpl == null) continue;
            entityAttributeInstanceImpl.invalidateCache();
        }
    }

    public Set<EntityAttributeInstance> getTrackedAttributes() {
        return this.trackedAttributes;
    }

    public Collection<EntityAttributeInstance> buildTrackedAttributesCollection() {
        HashSet<EntityAttributeInstance> set = Sets.newHashSet();
        for (EntityAttributeInstance entityAttributeInstance : this.values()) {
            if (!entityAttributeInstance.getAttribute().isTracked()) continue;
            set.add(entityAttributeInstance);
        }
        return set;
    }

    @Override
    public /* synthetic */ EntityAttributeInstance get(String name) {
        return this.get(name);
    }

    @Override
    public /* synthetic */ EntityAttributeInstance get(EntityAttribute attribute) {
        return this.get(attribute);
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.collection.LowercaseMap;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEntityAttributeContainer {
    protected final Map<EntityAttribute, EntityAttributeInstance> instancesByKey = Maps.newHashMap();
    protected final Map<String, EntityAttributeInstance> instancesById = new LowercaseMap<EntityAttributeInstance>();
    protected final Multimap<EntityAttribute, EntityAttribute> attributeHierarchy = HashMultimap.create();

    @Nullable
    public EntityAttributeInstance get(EntityAttribute attribute) {
        return this.instancesByKey.get(attribute);
    }

    @Nullable
    public EntityAttributeInstance get(String name) {
        return this.instancesById.get(name);
    }

    public EntityAttributeInstance register(EntityAttribute attribute) {
        if (this.instancesById.containsKey(attribute.getId())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        EntityAttributeInstance entityAttributeInstance = this.createInstance(attribute);
        this.instancesById.put(attribute.getId(), entityAttributeInstance);
        this.instancesByKey.put(attribute, entityAttributeInstance);
        for (EntityAttribute entityAttribute = attribute.getParent(); entityAttribute != null; entityAttribute = entityAttribute.getParent()) {
            this.attributeHierarchy.put(entityAttribute, attribute);
        }
        return entityAttributeInstance;
    }

    protected abstract EntityAttributeInstance createInstance(EntityAttribute var1);

    public Collection<EntityAttributeInstance> values() {
        return this.instancesById.values();
    }

    public void add(EntityAttributeInstance instance) {
    }

    public void removeAll(Multimap<String, EntityAttributeModifier> modifiers) {
        for (Map.Entry<String, EntityAttributeModifier> entry : modifiers.entries()) {
            EntityAttributeInstance entityAttributeInstance = this.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
        }
    }

    public void replaceAll(Multimap<String, EntityAttributeModifier> modifiers) {
        for (Map.Entry<String, EntityAttributeModifier> entry : modifiers.entries()) {
            EntityAttributeInstance entityAttributeInstance = this.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
            entityAttributeInstance.addModifier(entry.getValue());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void copyFrom(AbstractEntityAttributeContainer attributeContainer) {
        this.values().forEach(entityAttributeInstance -> {
            EntityAttributeInstance entityAttributeInstance2 = attributeContainer.get(entityAttributeInstance.getAttribute());
            if (entityAttributeInstance2 != null) {
                entityAttributeInstance.copyFrom(entityAttributeInstance2);
            }
        });
    }
}


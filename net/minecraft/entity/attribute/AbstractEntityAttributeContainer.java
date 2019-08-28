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
import net.minecraft.util.LowercaseMap;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEntityAttributeContainer {
    protected final Map<EntityAttribute, EntityAttributeInstance> instancesByKey = Maps.newHashMap();
    protected final Map<String, EntityAttributeInstance> instancesById = new LowercaseMap<EntityAttributeInstance>();
    protected final Multimap<EntityAttribute, EntityAttribute> attributeHierarchy = HashMultimap.create();

    @Nullable
    public EntityAttributeInstance get(EntityAttribute entityAttribute) {
        return this.instancesByKey.get(entityAttribute);
    }

    @Nullable
    public EntityAttributeInstance get(String string) {
        return this.instancesById.get(string);
    }

    public EntityAttributeInstance register(EntityAttribute entityAttribute) {
        if (this.instancesById.containsKey(entityAttribute.getId())) {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        EntityAttributeInstance entityAttributeInstance = this.createInstance(entityAttribute);
        this.instancesById.put(entityAttribute.getId(), entityAttributeInstance);
        this.instancesByKey.put(entityAttribute, entityAttributeInstance);
        for (EntityAttribute entityAttribute2 = entityAttribute.getParent(); entityAttribute2 != null; entityAttribute2 = entityAttribute2.getParent()) {
            this.attributeHierarchy.put(entityAttribute2, entityAttribute);
        }
        return entityAttributeInstance;
    }

    protected abstract EntityAttributeInstance createInstance(EntityAttribute var1);

    public Collection<EntityAttributeInstance> values() {
        return this.instancesById.values();
    }

    public void add(EntityAttributeInstance entityAttributeInstance) {
    }

    public void removeAll(Multimap<String, EntityAttributeModifier> multimap) {
        for (Map.Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
            EntityAttributeInstance entityAttributeInstance = this.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
        }
    }

    public void replaceAll(Multimap<String, EntityAttributeModifier> multimap) {
        for (Map.Entry<String, EntityAttributeModifier> entry : multimap.entries()) {
            EntityAttributeInstance entityAttributeInstance = this.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
            entityAttributeInstance.addModifier(entry.getValue());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void method_22324(AbstractEntityAttributeContainer abstractEntityAttributeContainer) {
        this.values().forEach(entityAttributeInstance -> {
            EntityAttributeInstance entityAttributeInstance2 = abstractEntityAttributeContainer.get(entityAttributeInstance.getAttribute());
            if (entityAttributeInstance2 != null) {
                entityAttributeInstance.method_22323(entityAttributeInstance2);
            }
        });
    }
}


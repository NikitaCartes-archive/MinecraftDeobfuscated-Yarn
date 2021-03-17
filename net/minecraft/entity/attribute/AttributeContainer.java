/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class AttributeContainer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<EntityAttribute, EntityAttributeInstance> custom = Maps.newHashMap();
    private final Set<EntityAttributeInstance> tracked = Sets.newHashSet();
    private final DefaultAttributeContainer fallback;

    public AttributeContainer(DefaultAttributeContainer defaultAttributes) {
        this.fallback = defaultAttributes;
    }

    private void updateTrackedStatus(EntityAttributeInstance instance) {
        if (instance.getAttribute().isTracked()) {
            this.tracked.add(instance);
        }
    }

    public Set<EntityAttributeInstance> getTracked() {
        return this.tracked;
    }

    public Collection<EntityAttributeInstance> getAttributesToSend() {
        return this.custom.values().stream().filter(attribute -> attribute.getAttribute().isTracked()).collect(Collectors.toList());
    }

    @Nullable
    public EntityAttributeInstance getCustomInstance(EntityAttribute attribute2) {
        return this.custom.computeIfAbsent(attribute2, attribute -> this.fallback.createOverride(this::updateTrackedStatus, (EntityAttribute)attribute));
    }

    public boolean hasAttribute(EntityAttribute attribute) {
        return this.custom.get(attribute) != null || this.fallback.has(attribute);
    }

    public boolean hasModifierForAttribute(EntityAttribute attribute, UUID uuid) {
        EntityAttributeInstance entityAttributeInstance = this.custom.get(attribute);
        return entityAttributeInstance != null ? entityAttributeInstance.getModifier(uuid) != null : this.fallback.hasModifier(attribute, uuid);
    }

    public double getValue(EntityAttribute attribute) {
        EntityAttributeInstance entityAttributeInstance = this.custom.get(attribute);
        return entityAttributeInstance != null ? entityAttributeInstance.getValue() : this.fallback.getValue(attribute);
    }

    public double getBaseValue(EntityAttribute attribute) {
        EntityAttributeInstance entityAttributeInstance = this.custom.get(attribute);
        return entityAttributeInstance != null ? entityAttributeInstance.getBaseValue() : this.fallback.getBaseValue(attribute);
    }

    public double getModifierValue(EntityAttribute attribute, UUID uuid) {
        EntityAttributeInstance entityAttributeInstance = this.custom.get(attribute);
        return entityAttributeInstance != null ? entityAttributeInstance.getModifier(uuid).getValue() : this.fallback.getModifierValue(attribute, uuid);
    }

    public void removeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        attributeModifiers.asMap().forEach((entityAttribute, collection) -> {
            EntityAttributeInstance entityAttributeInstance = this.custom.get(entityAttribute);
            if (entityAttributeInstance != null) {
                collection.forEach(entityAttributeInstance::removeModifier);
            }
        });
    }

    public void addTemporaryModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        attributeModifiers.forEach((entityAttribute, entityAttributeModifier) -> {
            EntityAttributeInstance entityAttributeInstance = this.getCustomInstance((EntityAttribute)entityAttribute);
            if (entityAttributeInstance != null) {
                entityAttributeInstance.removeModifier((EntityAttributeModifier)entityAttributeModifier);
                entityAttributeInstance.addTemporaryModifier((EntityAttributeModifier)entityAttributeModifier);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public void setFrom(AttributeContainer other) {
        other.custom.values().forEach(entityAttributeInstance -> {
            EntityAttributeInstance entityAttributeInstance2 = this.getCustomInstance(entityAttributeInstance.getAttribute());
            if (entityAttributeInstance2 != null) {
                entityAttributeInstance2.setFrom((EntityAttributeInstance)entityAttributeInstance);
            }
        });
    }

    public NbtList toNbt() {
        NbtList nbtList = new NbtList();
        for (EntityAttributeInstance entityAttributeInstance : this.custom.values()) {
            nbtList.add(entityAttributeInstance.toNbt());
        }
        return nbtList;
    }

    public void readNbt(NbtList tag) {
        for (int i = 0; i < tag.size(); ++i) {
            NbtCompound nbtCompound = tag.getCompound(i);
            String string = nbtCompound.getString("Name");
            Util.ifPresentOrElse(Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(string)), entityAttribute -> {
                EntityAttributeInstance entityAttributeInstance = this.getCustomInstance((EntityAttribute)entityAttribute);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.readNbt(nbtCompound);
                }
            }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", (Object)string));
        }
    }
}


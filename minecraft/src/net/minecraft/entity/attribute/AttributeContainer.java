package net.minecraft.entity.attribute;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.slf4j.Logger;

public class AttributeContainer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<EntityAttribute, EntityAttributeInstance> custom = Maps.<EntityAttribute, EntityAttributeInstance>newHashMap();
	private final Set<EntityAttributeInstance> tracked = Sets.<EntityAttributeInstance>newHashSet();
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
		return (Collection<EntityAttributeInstance>)this.custom
			.values()
			.stream()
			.filter(attribute -> attribute.getAttribute().isTracked())
			.collect(Collectors.toList());
	}

	@Nullable
	public EntityAttributeInstance getCustomInstance(EntityAttribute attribute) {
		return (EntityAttributeInstance)this.custom.computeIfAbsent(attribute, attributex -> this.fallback.createOverride(this::updateTrackedStatus, attributex));
	}

	@Nullable
	public EntityAttributeInstance method_45329(RegistryEntry<EntityAttribute> registryEntry) {
		return this.getCustomInstance(registryEntry.value());
	}

	public boolean hasAttribute(EntityAttribute attribute) {
		return this.custom.get(attribute) != null || this.fallback.has(attribute);
	}

	public boolean method_45331(RegistryEntry<EntityAttribute> registryEntry) {
		return this.hasAttribute(registryEntry.value());
	}

	public boolean hasModifierForAttribute(EntityAttribute attribute, UUID uuid) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getModifier(uuid) != null : this.fallback.hasModifier(attribute, uuid);
	}

	public boolean method_45330(RegistryEntry<EntityAttribute> registryEntry, UUID uUID) {
		return this.hasModifierForAttribute(registryEntry.value(), uUID);
	}

	public double getValue(EntityAttribute attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getValue() : this.fallback.getValue(attribute);
	}

	public double getBaseValue(EntityAttribute attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getBaseValue() : this.fallback.getBaseValue(attribute);
	}

	public double getModifierValue(EntityAttribute attribute, UUID uuid) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getModifier(uuid).getValue() : this.fallback.getModifierValue(attribute, uuid);
	}

	public double method_45332(RegistryEntry<EntityAttribute> registryEntry, UUID uUID) {
		return this.getModifierValue(registryEntry.value(), uUID);
	}

	public void removeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
		attributeModifiers.asMap().forEach((attribute, modifiers) -> {
			EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
			if (entityAttributeInstance != null) {
				modifiers.forEach(entityAttributeInstance::removeModifier);
			}
		});
	}

	public void addTemporaryModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
		attributeModifiers.forEach((attribute, attributeModifier) -> {
			EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attribute);
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(attributeModifier);
				entityAttributeInstance.addTemporaryModifier(attributeModifier);
			}
		});
	}

	public void setFrom(AttributeContainer other) {
		other.custom.values().forEach(attributeInstance -> {
			EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attributeInstance.getAttribute());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.setFrom(attributeInstance);
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

	public void readNbt(NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			NbtCompound nbtCompound = nbt.getCompound(i);
			String string = nbtCompound.getString("Name");
			Util.ifPresentOrElse(Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(string)), attribute -> {
				EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attribute);
				if (entityAttributeInstance != null) {
					entityAttributeInstance.readNbt(nbtCompound);
				}
			}, () -> LOGGER.warn("Ignoring unknown attribute '{}'", string));
		}
	}
}

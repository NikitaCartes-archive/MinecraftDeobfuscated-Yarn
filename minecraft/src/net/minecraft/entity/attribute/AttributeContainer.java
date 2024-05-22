package net.minecraft.entity.attribute;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class AttributeContainer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> custom = new Object2ObjectOpenHashMap<>();
	private final Set<EntityAttributeInstance> tracked = new ObjectOpenHashSet<>();
	private final Set<EntityAttributeInstance> pendingUpdate = new ObjectOpenHashSet<>();
	private final DefaultAttributeContainer fallback;

	public AttributeContainer(DefaultAttributeContainer defaultAttributes) {
		this.fallback = defaultAttributes;
	}

	private void updateTrackedStatus(EntityAttributeInstance instance) {
		this.pendingUpdate.add(instance);
		if (instance.getAttribute().value().isTracked()) {
			this.tracked.add(instance);
		}
	}

	public Set<EntityAttributeInstance> getTracked() {
		return this.tracked;
	}

	public Set<EntityAttributeInstance> getPendingUpdate() {
		return this.pendingUpdate;
	}

	public Collection<EntityAttributeInstance> getAttributesToSend() {
		return (Collection<EntityAttributeInstance>)this.custom
			.values()
			.stream()
			.filter(attribute -> attribute.getAttribute().value().isTracked())
			.collect(Collectors.toList());
	}

	@Nullable
	public EntityAttributeInstance getCustomInstance(RegistryEntry<EntityAttribute> attribute) {
		return (EntityAttributeInstance)this.custom.computeIfAbsent(attribute, attributex -> this.fallback.createOverride(this::updateTrackedStatus, attributex));
	}

	public boolean hasAttribute(RegistryEntry<EntityAttribute> attribute) {
		return this.custom.get(attribute) != null || this.fallback.has(attribute);
	}

	public boolean hasModifierForAttribute(RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getModifier(identifier) != null : this.fallback.hasModifier(attribute, identifier);
	}

	public double getValue(RegistryEntry<EntityAttribute> attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getValue() : this.fallback.getValue(attribute);
	}

	public double getBaseValue(RegistryEntry<EntityAttribute> attribute) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getBaseValue() : this.fallback.getBaseValue(attribute);
	}

	public double getModifierValue(RegistryEntry<EntityAttribute> attribute, Identifier identifier) {
		EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
		return entityAttributeInstance != null ? entityAttributeInstance.getModifier(identifier).value() : this.fallback.getModifierValue(attribute, identifier);
	}

	public void addTemporaryModifiers(Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersMap) {
		modifiersMap.forEach((attribute, modifier) -> {
			EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attribute);
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(modifier.uuid());
				entityAttributeInstance.addTemporaryModifier(modifier);
			}
		});
	}

	public void removeModifiers(Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiersMap) {
		modifiersMap.asMap().forEach((attribute, modifiers) -> {
			EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
			if (entityAttributeInstance != null) {
				modifiers.forEach(modifier -> entityAttributeInstance.removeModifier(modifier.uuid()));
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

	public void setBaseFrom(AttributeContainer other) {
		other.custom.values().forEach(attributeInstance -> {
			EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attributeInstance.getAttribute());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.setBaseValue(attributeInstance.getBaseValue());
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
			String string = nbtCompound.getString("id");
			Identifier identifier = Identifier.tryParse(string);
			if (identifier != null) {
				Util.ifPresentOrElse(Registries.ATTRIBUTE.getEntry(identifier), attribute -> {
					EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attribute);
					if (entityAttributeInstance != null) {
						entityAttributeInstance.readNbt(nbtCompound);
					}
				}, () -> LOGGER.warn("Ignoring unknown attribute '{}'", identifier));
			} else {
				LOGGER.warn("Ignoring malformed attribute '{}'", string);
			}
		}
	}
}

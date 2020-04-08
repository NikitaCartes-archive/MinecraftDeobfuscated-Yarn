package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.registry.Registry;

public class EntityAttributeInstance {
	private final EntityAttribute type;
	private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(
		EntityAttributeModifier.Operation.class
	);
	private final Map<UUID, EntityAttributeModifier> byId = new Object2ObjectArrayMap<>();
	private final Set<EntityAttributeModifier> persistentModifiers = new ObjectArraySet<>();
	private double baseValue;
	private boolean dirty = true;
	private double value;
	private final Consumer<EntityAttributeInstance> updateCallback;

	public EntityAttributeInstance(EntityAttribute type, Consumer<EntityAttributeInstance> updateCallback) {
		this.type = type;
		this.updateCallback = updateCallback;
		this.baseValue = type.getDefaultValue();
	}

	public EntityAttribute getAttribute() {
		return this.type;
	}

	public double getBaseValue() {
		return this.baseValue;
	}

	public void setBaseValue(double baseValue) {
		if (baseValue != this.baseValue) {
			this.baseValue = baseValue;
			this.onUpdate();
		}
	}

	public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
		return (Set<EntityAttributeModifier>)this.operationToModifiers.computeIfAbsent(operation, operationx -> Sets.newHashSet());
	}

	public Set<EntityAttributeModifier> getModifiers() {
		return ImmutableSet.copyOf(this.byId.values());
	}

	@Nullable
	public EntityAttributeModifier getModifier(UUID uuid) {
		return (EntityAttributeModifier)this.byId.get(uuid);
	}

	public boolean hasModifier(EntityAttributeModifier modifier) {
		return this.byId.get(modifier.getId()) != null;
	}

	private void addModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.byId.putIfAbsent(modifier.getId(), modifier);
		if (entityAttributeModifier != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			this.getModifiers(modifier.getOperation()).add(modifier);
			this.onUpdate();
		}
	}

	public void addTemporaryModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
	}

	public void addPersistentModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
		this.persistentModifiers.add(modifier);
	}

	protected void onUpdate() {
		this.dirty = true;
		this.updateCallback.accept(this);
	}

	public void removeModifier(EntityAttributeModifier modifier) {
		this.getModifiers(modifier.getOperation()).remove(modifier);
		this.byId.remove(modifier.getId());
		this.persistentModifiers.remove(modifier);
		this.onUpdate();
	}

	public void removeModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
		if (entityAttributeModifier != null) {
			this.removeModifier(entityAttributeModifier);
		}
	}

	@Environment(EnvType.CLIENT)
	public void clearModifiers() {
		for (EntityAttributeModifier entityAttributeModifier : this.getModifiers()) {
			this.removeModifier(entityAttributeModifier);
		}
	}

	public double getValue() {
		if (this.dirty) {
			this.value = this.computeValue();
			this.dirty = false;
		}

		return this.value;
	}

	private double computeValue() {
		double d = this.getBaseValue();

		for (EntityAttributeModifier entityAttributeModifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
			d += entityAttributeModifier.getAmount();
		}

		double e = d;

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
			e += d * entityAttributeModifier2.getAmount();
		}

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
			e *= 1.0 + entityAttributeModifier2.getAmount();
		}

		return this.type.clamp(e);
	}

	private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
		return (Collection<EntityAttributeModifier>)this.operationToModifiers.getOrDefault(operation, Collections.emptySet());
	}

	public void setFrom(EntityAttributeInstance other) {
		this.baseValue = other.baseValue;
		this.byId.clear();
		this.byId.putAll(other.byId);
		this.persistentModifiers.clear();
		this.persistentModifiers.addAll(other.persistentModifiers);
		this.operationToModifiers.clear();
		other.operationToModifiers.forEach((operation, set) -> this.getModifiers(operation).addAll(set));
		this.onUpdate();
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", Registry.ATTRIBUTES.getId(this.type).toString());
		compoundTag.putDouble("Base", this.baseValue);
		if (!this.persistentModifiers.isEmpty()) {
			ListTag listTag = new ListTag();

			for (EntityAttributeModifier entityAttributeModifier : this.persistentModifiers) {
				listTag.add(entityAttributeModifier.toTag());
			}

			compoundTag.put("Modifiers", listTag);
		}

		return compoundTag;
	}

	public void fromTag(CompoundTag tag) {
		this.baseValue = tag.getDouble("Base");
		if (tag.contains("Modifiers", 9)) {
			ListTag listTag = tag.getList("Modifiers", 10);

			for (int i = 0; i < listTag.size(); i++) {
				EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromTag(listTag.getCompound(i));
				if (entityAttributeModifier != null) {
					this.byId.put(entityAttributeModifier.getId(), entityAttributeModifier);
					this.getModifiers(entityAttributeModifier.getOperation()).add(entityAttributeModifier);
					this.persistentModifiers.add(entityAttributeModifier);
				}
			}
		}

		this.onUpdate();
	}
}

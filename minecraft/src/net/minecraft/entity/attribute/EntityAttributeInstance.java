package net.minecraft.entity.attribute;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * A double-valued attribute.
 */
public class EntityAttributeInstance {
	private final RegistryEntry<EntityAttribute> type;
	private final Map<EntityAttributeModifier.Operation, Map<UUID, EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(
		EntityAttributeModifier.Operation.class
	);
	private final Map<UUID, EntityAttributeModifier> idToModifiers = new Object2ObjectArrayMap();
	private final Map<UUID, EntityAttributeModifier> persistentModifiers = new Object2ObjectArrayMap();
	private double baseValue;
	private boolean dirty = true;
	private double value;
	private final Consumer<EntityAttributeInstance> updateCallback;

	public EntityAttributeInstance(RegistryEntry<EntityAttribute> type, Consumer<EntityAttributeInstance> updateCallback) {
		this.type = type;
		this.updateCallback = updateCallback;
		this.baseValue = type.value().getDefaultValue();
	}

	public RegistryEntry<EntityAttribute> getAttribute() {
		return this.type;
	}

	/**
	 * Gets the base value of this attribute instance.
	 * This is the value before any attribute modifiers are applied.
	 */
	public double getBaseValue() {
		return this.baseValue;
	}

	public void setBaseValue(double baseValue) {
		if (baseValue != this.baseValue) {
			this.baseValue = baseValue;
			this.onUpdate();
		}
	}

	@VisibleForTesting
	Map<UUID, EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
		return (Map<UUID, EntityAttributeModifier>)this.operationToModifiers.computeIfAbsent(operation, operationx -> new Object2ObjectOpenHashMap());
	}

	public Set<EntityAttributeModifier> getModifiers() {
		return ImmutableSet.copyOf(this.idToModifiers.values());
	}

	@Nullable
	public EntityAttributeModifier getModifier(UUID uuid) {
		return (EntityAttributeModifier)this.idToModifiers.get(uuid);
	}

	public boolean hasModifier(EntityAttributeModifier modifier) {
		return this.idToModifiers.get(modifier.uuid()) != null;
	}

	private void addModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.putIfAbsent(modifier.uuid(), modifier);
		if (entityAttributeModifier != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			this.getModifiers(modifier.operation()).put(modifier.uuid(), modifier);
			this.onUpdate();
		}
	}

	public void updateModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.put(modifier.uuid(), modifier);
		if (modifier != entityAttributeModifier) {
			this.getModifiers(modifier.operation()).put(modifier.uuid(), modifier);
			this.onUpdate();
		}
	}

	/**
	 * Adds a temporary attribute modifier.
	 * The modifier will not be serialized.
	 */
	public void addTemporaryModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
	}

	public void addPersistentModifier(EntityAttributeModifier modifier) {
		this.addModifier(modifier);
		this.persistentModifiers.put(modifier.uuid(), modifier);
	}

	protected void onUpdate() {
		this.dirty = true;
		this.updateCallback.accept(this);
	}

	public void removeModifier(EntityAttributeModifier modifier) {
		this.removeModifier(modifier.uuid());
	}

	public void removeModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.remove(uuid);
		if (entityAttributeModifier != null) {
			this.getModifiers(entityAttributeModifier.operation()).remove(uuid);
			this.persistentModifiers.remove(uuid);
			this.onUpdate();
		}
	}

	public boolean tryRemoveModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.persistentModifiers.remove(uuid);
		if (entityAttributeModifier == null) {
			return false;
		} else {
			this.getModifiers(entityAttributeModifier.operation()).remove(entityAttributeModifier.uuid());
			this.idToModifiers.remove(uuid);
			this.onUpdate();
			return true;
		}
	}

	public void clearModifiers() {
		for(EntityAttributeModifier entityAttributeModifier : this.getModifiers()) {
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

	/**
	 * Computes this attribute's value, taking modifiers into account.
	 * 
	 * <p>Attribute modifiers are applied in order by operation:
	 * <ul><li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_VALUE ADD_VALUE} // Adds the value of the modifier to the attribute's base value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_MULTIPLIED_BASE ADD_MULTIPLIED_BASE} // Multiplies the value of the modifier to the attributes base value, and then adds it to the total value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADD_MULTIPLIED_TOTAL ADD_MULTIPLIED_TOTAL} // Adds 1 to the value of the attribute modifier. Then multiplies the attribute's value by the total value of the attribute after addition and multiplication of the base value occur.</li>
	 * </ul>
	 */
	private double computeValue() {
		double d = this.getBaseValue();

		for(EntityAttributeModifier entityAttributeModifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_VALUE)) {
			d += entityAttributeModifier.value();
		}

		double e = d;

		for(EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)) {
			e += d * entityAttributeModifier2.value();
		}

		for(EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) {
			e *= 1.0 + entityAttributeModifier2.value();
		}

		return this.type.value().clamp(e);
	}

	private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
		return ((Map)this.operationToModifiers.getOrDefault(operation, Map.of())).values();
	}

	/**
	 * Copies the values of an attribute to this attribute.
	 * 
	 * <p>Temporary modifiers are copied when using the operation.
	 */
	public void setFrom(EntityAttributeInstance other) {
		this.baseValue = other.baseValue;
		this.idToModifiers.clear();
		this.idToModifiers.putAll(other.idToModifiers);
		this.persistentModifiers.clear();
		this.persistentModifiers.putAll(other.persistentModifiers);
		this.operationToModifiers.clear();
		other.operationToModifiers.forEach((operation, modifiers) -> this.getModifiers(operation).putAll(modifiers));
		this.onUpdate();
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		RegistryKey<EntityAttribute> registryKey = (RegistryKey)this.type
			.getKey()
			.orElseThrow(() -> new IllegalStateException("Tried to serialize unregistered attribute"));
		nbtCompound.putString("Name", registryKey.getValue().toString());
		nbtCompound.putDouble("Base", this.baseValue);
		if (!this.persistentModifiers.isEmpty()) {
			NbtList nbtList = new NbtList();

			for(EntityAttributeModifier entityAttributeModifier : this.persistentModifiers.values()) {
				nbtList.add(entityAttributeModifier.toNbt());
			}

			nbtCompound.put("Modifiers", nbtList);
		}

		return nbtCompound;
	}

	public void readNbt(NbtCompound nbt) {
		this.baseValue = nbt.getDouble("Base");
		if (nbt.contains("Modifiers", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("Modifiers", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); ++i) {
				EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromNbt(nbtList.getCompound(i));
				if (entityAttributeModifier != null) {
					this.idToModifiers.put(entityAttributeModifier.uuid(), entityAttributeModifier);
					this.getModifiers(entityAttributeModifier.operation()).put(entityAttributeModifier.uuid(), entityAttributeModifier);
					this.persistentModifiers.put(entityAttributeModifier.uuid(), entityAttributeModifier);
				}
			}
		}

		this.onUpdate();
	}
}

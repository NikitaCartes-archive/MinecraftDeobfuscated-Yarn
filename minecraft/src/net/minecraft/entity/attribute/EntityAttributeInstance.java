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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;

/**
 * A double-valued attribute.
 */
public class EntityAttributeInstance {
	private final EntityAttribute type;
	private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(
		EntityAttributeModifier.Operation.class
	);
	private final Map<UUID, EntityAttributeModifier> idToModifiers = new Object2ObjectArrayMap<>();
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

	public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
		return (Set<EntityAttributeModifier>)this.operationToModifiers.computeIfAbsent(operation, operationx -> Sets.newHashSet());
	}

	public Set<EntityAttributeModifier> getModifiers() {
		return ImmutableSet.copyOf(this.idToModifiers.values());
	}

	@Nullable
	public EntityAttributeModifier getModifier(UUID uuid) {
		return (EntityAttributeModifier)this.idToModifiers.get(uuid);
	}

	public boolean hasModifier(EntityAttributeModifier modifier) {
		return this.idToModifiers.get(modifier.getId()) != null;
	}

	private void addModifier(EntityAttributeModifier modifier) {
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.putIfAbsent(modifier.getId(), modifier);
		if (entityAttributeModifier != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			this.getModifiers(modifier.getOperation()).add(modifier);
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
		this.persistentModifiers.add(modifier);
	}

	protected void onUpdate() {
		this.dirty = true;
		this.updateCallback.accept(this);
	}

	public void removeModifier(EntityAttributeModifier modifier) {
		this.getModifiers(modifier.getOperation()).remove(modifier);
		this.idToModifiers.remove(modifier.getId());
		this.persistentModifiers.remove(modifier);
		this.onUpdate();
	}

	public void removeModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
		if (entityAttributeModifier != null) {
			this.removeModifier(entityAttributeModifier);
		}
	}

	public boolean tryRemoveModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
		if (entityAttributeModifier != null && this.persistentModifiers.contains(entityAttributeModifier)) {
			this.removeModifier(entityAttributeModifier);
			return true;
		} else {
			return false;
		}
	}

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

	/**
	 * Computes this attribute's value, taking modifiers into account.
	 * 
	 * <p>Attribute modifiers are applied in order by operation:
	 * <ul><li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#ADDITION ADDITION} // Adds the value of the modifier to the attribute's base value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#MULTIPLY_BASE MULTIPLY_BASE} // Multiplies the value of the modifier to the attributes base value, and then adds it to the total value.</li>
	 * <li>{@link net.minecraft.entity.attribute.EntityAttributeModifier.Operation#MULTIPLY_TOTAL MULTIPLY_TOTAL} // Adds 1 to the value of the attribute modifier. Then multiplies the attribute's value by the total value of the attribute after addition and multiplication of the base value occur.</li>
	 * </ul>
	 */
	private double computeValue() {
		double d = this.getBaseValue();

		for (EntityAttributeModifier entityAttributeModifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
			d += entityAttributeModifier.getValue();
		}

		double e = d;

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
			e += d * entityAttributeModifier2.getValue();
		}

		for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
			e *= 1.0 + entityAttributeModifier2.getValue();
		}

		return this.type.clamp(e);
	}

	private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
		return (Collection<EntityAttributeModifier>)this.operationToModifiers.getOrDefault(operation, Collections.emptySet());
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
		this.persistentModifiers.addAll(other.persistentModifiers);
		this.operationToModifiers.clear();
		other.operationToModifiers.forEach((operation, modifiers) -> this.getModifiers(operation).addAll(modifiers));
		this.onUpdate();
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Registries.ATTRIBUTE.getId(this.type).toString());
		nbtCompound.putDouble("Base", this.baseValue);
		if (!this.persistentModifiers.isEmpty()) {
			NbtList nbtList = new NbtList();

			for (EntityAttributeModifier entityAttributeModifier : this.persistentModifiers) {
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

			for (int i = 0; i < nbtList.size(); i++) {
				EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromNbt(nbtList.getCompound(i));
				if (entityAttributeModifier != null) {
					this.idToModifiers.put(entityAttributeModifier.getId(), entityAttributeModifier);
					this.getModifiers(entityAttributeModifier.getOperation()).add(entityAttributeModifier);
					this.persistentModifiers.add(entityAttributeModifier);
				}
			}
		}

		this.onUpdate();
	}
}

package net.minecraft.entity.attribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class EntityAttributeInstanceImpl implements EntityAttributeInstance {
	private final AbstractEntityAttributeContainer container;
	private final EntityAttribute attribute;
	private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> modifiersByOperation = Maps.newEnumMap(
		EntityAttributeModifier.Operation.class
	);
	private final Map<String, Set<EntityAttributeModifier>> modifiersByName = Maps.<String, Set<EntityAttributeModifier>>newHashMap();
	private final Map<UUID, EntityAttributeModifier> modifiersByUuid = Maps.<UUID, EntityAttributeModifier>newHashMap();
	private double baseValue;
	private boolean needsRefresh = true;
	private double cachedValue;

	public EntityAttributeInstanceImpl(AbstractEntityAttributeContainer container, EntityAttribute attribute) {
		this.container = container;
		this.attribute = attribute;
		this.baseValue = attribute.getDefaultValue();

		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			this.modifiersByOperation.put(operation, Sets.newHashSet());
		}
	}

	@Override
	public EntityAttribute getAttribute() {
		return this.attribute;
	}

	@Override
	public double getBaseValue() {
		return this.baseValue;
	}

	@Override
	public void setBaseValue(double baseValue) {
		if (baseValue != this.getBaseValue()) {
			this.baseValue = baseValue;
			this.invalidateCache();
		}
	}

	@Override
	public Collection<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
		return (Collection<EntityAttributeModifier>)this.modifiersByOperation.get(operation);
	}

	@Override
	public Collection<EntityAttributeModifier> getModifiers() {
		Set<EntityAttributeModifier> set = Sets.<EntityAttributeModifier>newHashSet();

		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			set.addAll(this.getModifiers(operation));
		}

		return set;
	}

	@Nullable
	@Override
	public EntityAttributeModifier getModifier(UUID uuid) {
		return (EntityAttributeModifier)this.modifiersByUuid.get(uuid);
	}

	@Override
	public boolean hasModifier(EntityAttributeModifier modifier) {
		return this.modifiersByUuid.get(modifier.getId()) != null;
	}

	@Override
	public void addModifier(EntityAttributeModifier modifier) {
		if (this.getModifier(modifier.getId()) != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.modifiersByName.computeIfAbsent(modifier.getName(), string -> Sets.newHashSet());
			((Set)this.modifiersByOperation.get(modifier.getOperation())).add(modifier);
			set.add(modifier);
			this.modifiersByUuid.put(modifier.getId(), modifier);
			this.invalidateCache();
		}
	}

	protected void invalidateCache() {
		this.needsRefresh = true;
		this.container.add(this);
	}

	@Override
	public void removeModifier(EntityAttributeModifier modifier) {
		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			((Set)this.modifiersByOperation.get(operation)).remove(modifier);
		}

		Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.modifiersByName.get(modifier.getName());
		if (set != null) {
			set.remove(modifier);
			if (set.isEmpty()) {
				this.modifiersByName.remove(modifier.getName());
			}
		}

		this.modifiersByUuid.remove(modifier.getId());
		this.invalidateCache();
	}

	@Override
	public void removeModifier(UUID uuid) {
		EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
		if (entityAttributeModifier != null) {
			this.removeModifier(entityAttributeModifier);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clearModifiers() {
		Collection<EntityAttributeModifier> collection = this.getModifiers();
		if (collection != null) {
			for (EntityAttributeModifier entityAttributeModifier : Lists.newArrayList(collection)) {
				this.removeModifier(entityAttributeModifier);
			}
		}
	}

	@Override
	public double getValue() {
		if (this.needsRefresh) {
			this.cachedValue = this.computeValue();
			this.needsRefresh = false;
		}

		return this.cachedValue;
	}

	private double computeValue() {
		double d = this.getBaseValue();

		for (EntityAttributeModifier entityAttributeModifier : this.getAllModifiers(EntityAttributeModifier.Operation.ADDITION)) {
			d += entityAttributeModifier.getAmount();
		}

		double e = d;

		for (EntityAttributeModifier entityAttributeModifier2 : this.getAllModifiers(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
			e += d * entityAttributeModifier2.getAmount();
		}

		for (EntityAttributeModifier entityAttributeModifier2 : this.getAllModifiers(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
			e *= 1.0 + entityAttributeModifier2.getAmount();
		}

		return this.attribute.clamp(e);
	}

	private Collection<EntityAttributeModifier> getAllModifiers(EntityAttributeModifier.Operation operation) {
		Set<EntityAttributeModifier> set = Sets.<EntityAttributeModifier>newHashSet(this.getModifiers(operation));

		for (EntityAttribute entityAttribute = this.attribute.getParent(); entityAttribute != null; entityAttribute = entityAttribute.getParent()) {
			EntityAttributeInstance entityAttributeInstance = this.container.get(entityAttribute);
			if (entityAttributeInstance != null) {
				set.addAll(entityAttributeInstance.getModifiers(operation));
			}
		}

		return set;
	}
}

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

	public EntityAttributeInstanceImpl(AbstractEntityAttributeContainer abstractEntityAttributeContainer, EntityAttribute entityAttribute) {
		this.container = abstractEntityAttributeContainer;
		this.attribute = entityAttribute;
		this.baseValue = entityAttribute.getDefaultValue();

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
	public void setBaseValue(double d) {
		if (d != this.getBaseValue()) {
			this.baseValue = d;
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
	public EntityAttributeModifier getModifier(UUID uUID) {
		return (EntityAttributeModifier)this.modifiersByUuid.get(uUID);
	}

	@Override
	public boolean hasModifier(EntityAttributeModifier entityAttributeModifier) {
		return this.modifiersByUuid.get(entityAttributeModifier.getId()) != null;
	}

	@Override
	public void addModifier(EntityAttributeModifier entityAttributeModifier) {
		if (this.getModifier(entityAttributeModifier.getId()) != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.modifiersByName
				.computeIfAbsent(entityAttributeModifier.getName(), string -> Sets.newHashSet());
			((Set)this.modifiersByOperation.get(entityAttributeModifier.getOperation())).add(entityAttributeModifier);
			set.add(entityAttributeModifier);
			this.modifiersByUuid.put(entityAttributeModifier.getId(), entityAttributeModifier);
			this.invalidateCache();
		}
	}

	protected void invalidateCache() {
		this.needsRefresh = true;
		this.container.add(this);
	}

	@Override
	public void removeModifier(EntityAttributeModifier entityAttributeModifier) {
		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			((Set)this.modifiersByOperation.get(operation)).remove(entityAttributeModifier);
		}

		Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.modifiersByName.get(entityAttributeModifier.getName());
		if (set != null) {
			set.remove(entityAttributeModifier);
			if (set.isEmpty()) {
				this.modifiersByName.remove(entityAttributeModifier.getName());
			}
		}

		this.modifiersByUuid.remove(entityAttributeModifier.getId());
		this.invalidateCache();
	}

	@Override
	public void removeModifier(UUID uUID) {
		EntityAttributeModifier entityAttributeModifier = this.getModifier(uUID);
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

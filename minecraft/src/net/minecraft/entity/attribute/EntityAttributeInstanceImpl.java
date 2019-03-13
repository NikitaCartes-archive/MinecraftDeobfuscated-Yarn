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
	private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> field_6347 = Maps.newEnumMap(EntityAttributeModifier.Operation.class);
	private final Map<String, Set<EntityAttributeModifier>> field_6345 = Maps.<String, Set<EntityAttributeModifier>>newHashMap();
	private final Map<UUID, EntityAttributeModifier> field_6343 = Maps.<UUID, EntityAttributeModifier>newHashMap();
	private double baseValue;
	private boolean field_6344 = true;
	private double field_6348;

	public EntityAttributeInstanceImpl(AbstractEntityAttributeContainer abstractEntityAttributeContainer, EntityAttribute entityAttribute) {
		this.container = abstractEntityAttributeContainer;
		this.attribute = entityAttribute;
		this.baseValue = entityAttribute.getDefaultValue();

		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			this.field_6347.put(operation, Sets.newHashSet());
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
			this.method_6217();
		}
	}

	@Override
	public Collection<EntityAttributeModifier> method_6193(EntityAttributeModifier.Operation operation) {
		return (Collection<EntityAttributeModifier>)this.field_6347.get(operation);
	}

	@Override
	public Collection<EntityAttributeModifier> getModifiers() {
		Set<EntityAttributeModifier> set = Sets.<EntityAttributeModifier>newHashSet();

		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			set.addAll(this.method_6193(operation));
		}

		return set;
	}

	@Nullable
	@Override
	public EntityAttributeModifier method_6199(UUID uUID) {
		return (EntityAttributeModifier)this.field_6343.get(uUID);
	}

	@Override
	public boolean method_6196(EntityAttributeModifier entityAttributeModifier) {
		return this.field_6343.get(entityAttributeModifier.getId()) != null;
	}

	@Override
	public void method_6197(EntityAttributeModifier entityAttributeModifier) {
		if (this.method_6199(entityAttributeModifier.getId()) != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.field_6345
				.computeIfAbsent(entityAttributeModifier.getName(), string -> Sets.newHashSet());
			((Set)this.field_6347.get(entityAttributeModifier.getOperation())).add(entityAttributeModifier);
			set.add(entityAttributeModifier);
			this.field_6343.put(entityAttributeModifier.getId(), entityAttributeModifier);
			this.method_6217();
		}
	}

	protected void method_6217() {
		this.field_6344 = true;
		this.container.method_6211(this);
	}

	@Override
	public void method_6202(EntityAttributeModifier entityAttributeModifier) {
		for (EntityAttributeModifier.Operation operation : EntityAttributeModifier.Operation.values()) {
			((Set)this.field_6347.get(operation)).remove(entityAttributeModifier);
		}

		Set<EntityAttributeModifier> set = (Set<EntityAttributeModifier>)this.field_6345.get(entityAttributeModifier.getName());
		if (set != null) {
			set.remove(entityAttributeModifier);
			if (set.isEmpty()) {
				this.field_6345.remove(entityAttributeModifier.getName());
			}
		}

		this.field_6343.remove(entityAttributeModifier.getId());
		this.method_6217();
	}

	@Override
	public void removeModifier(UUID uUID) {
		EntityAttributeModifier entityAttributeModifier = this.method_6199(uUID);
		if (entityAttributeModifier != null) {
			this.method_6202(entityAttributeModifier);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clearModifiers() {
		Collection<EntityAttributeModifier> collection = this.getModifiers();
		if (collection != null) {
			for (EntityAttributeModifier entityAttributeModifier : Lists.newArrayList(collection)) {
				this.method_6202(entityAttributeModifier);
			}
		}
	}

	@Override
	public double getValue() {
		if (this.field_6344) {
			this.field_6348 = this.method_6220();
			this.field_6344 = false;
		}

		return this.field_6348;
	}

	private double method_6220() {
		double d = this.getBaseValue();

		for (EntityAttributeModifier entityAttributeModifier : this.method_6218(EntityAttributeModifier.Operation.field_6328)) {
			d += entityAttributeModifier.getAmount();
		}

		double e = d;

		for (EntityAttributeModifier entityAttributeModifier2 : this.method_6218(EntityAttributeModifier.Operation.field_6330)) {
			e += d * entityAttributeModifier2.getAmount();
		}

		for (EntityAttributeModifier entityAttributeModifier2 : this.method_6218(EntityAttributeModifier.Operation.field_6331)) {
			e *= 1.0 + entityAttributeModifier2.getAmount();
		}

		return this.attribute.method_6165(e);
	}

	private Collection<EntityAttributeModifier> method_6218(EntityAttributeModifier.Operation operation) {
		Set<EntityAttributeModifier> set = Sets.<EntityAttributeModifier>newHashSet(this.method_6193(operation));

		for (EntityAttribute entityAttribute = this.attribute.getParent(); entityAttribute != null; entityAttribute = entityAttribute.getParent()) {
			EntityAttributeInstance entityAttributeInstance = this.container.get(entityAttribute);
			if (entityAttributeInstance != null) {
				set.addAll(entityAttributeInstance.method_6193(operation));
			}
		}

		return set;
	}
}

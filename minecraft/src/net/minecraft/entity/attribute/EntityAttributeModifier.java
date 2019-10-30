package net.minecraft.entity.attribute;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;

public class EntityAttributeModifier {
	private final double amount;
	private final EntityAttributeModifier.Operation operation;
	private final Supplier<String> nameGetter;
	private final UUID uuid;
	private boolean serialize = true;

	public EntityAttributeModifier(String name, double amount, EntityAttributeModifier.Operation operation) {
		this(MathHelper.randomUuid(ThreadLocalRandom.current()), (Supplier<String>)(() -> name), amount, operation);
	}

	public EntityAttributeModifier(UUID uuid, String name, double amount, EntityAttributeModifier.Operation operation) {
		this(uuid, (Supplier<String>)(() -> name), amount, operation);
	}

	public EntityAttributeModifier(UUID uuid, Supplier<String> nameGetter, double amount, EntityAttributeModifier.Operation operation) {
		this.uuid = uuid;
		this.nameGetter = nameGetter;
		this.amount = amount;
		this.operation = operation;
	}

	public UUID getId() {
		return this.uuid;
	}

	public String getName() {
		return (String)this.nameGetter.get();
	}

	public EntityAttributeModifier.Operation getOperation() {
		return this.operation;
	}

	public double getAmount() {
		return this.amount;
	}

	public boolean shouldSerialize() {
		return this.serialize;
	}

	public EntityAttributeModifier setSerialize(boolean serialize) {
		this.serialize = serialize;
		return this;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)o;
			return Objects.equals(this.uuid, entityAttributeModifier.uuid);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.uuid != null ? this.uuid.hashCode() : 0;
	}

	public String toString() {
		return "AttributeModifier{amount="
			+ this.amount
			+ ", operation="
			+ this.operation
			+ ", name='"
			+ (String)this.nameGetter.get()
			+ '\''
			+ ", id="
			+ this.uuid
			+ ", serialize="
			+ this.serialize
			+ '}';
	}

	public static enum Operation {
		ADDITION(0),
		MULTIPLY_BASE(1),
		MULTIPLY_TOTAL(2);

		private static final EntityAttributeModifier.Operation[] VALUES = new EntityAttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
		private final int id;

		private Operation(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static EntityAttributeModifier.Operation fromId(int id) {
			if (id >= 0 && id < VALUES.length) {
				return VALUES[id];
			} else {
				throw new IllegalArgumentException("No operation with value " + id);
			}
		}
	}
}

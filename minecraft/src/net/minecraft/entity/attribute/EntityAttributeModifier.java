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

	public EntityAttributeModifier(String string, double d, EntityAttributeModifier.Operation operation) {
		this(MathHelper.randomUuid(ThreadLocalRandom.current()), (Supplier<String>)(() -> string), d, operation);
	}

	public EntityAttributeModifier(UUID uUID, String string, double d, EntityAttributeModifier.Operation operation) {
		this(uUID, (Supplier<String>)(() -> string), d, operation);
	}

	public EntityAttributeModifier(UUID uUID, Supplier<String> supplier, double d, EntityAttributeModifier.Operation operation) {
		this.uuid = uUID;
		this.nameGetter = supplier;
		this.amount = d;
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

	public EntityAttributeModifier setSerialize(boolean bl) {
		this.serialize = bl;
		return this;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)object;
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
		field_6328(0),
		field_6330(1),
		field_6331(2);

		private static final EntityAttributeModifier.Operation[] VALUES = new EntityAttributeModifier.Operation[]{field_6328, field_6330, field_6331};
		private final int id;

		private Operation(int j) {
			this.id = j;
		}

		public int getId() {
			return this.id;
		}

		public static EntityAttributeModifier.Operation fromId(int i) {
			if (i >= 0 && i < VALUES.length) {
				return VALUES[i];
			} else {
				throw new IllegalArgumentException("No operation with value " + i);
			}
		}
	}
}

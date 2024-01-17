package net.minecraft.entity.attribute;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.UUID;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class EntityAttributeModifier {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<EntityAttributeModifier> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("UUID").forGetter(EntityAttributeModifier::getId),
					Codec.STRING.fieldOf("Name").forGetter(modifier -> modifier.name),
					Codec.DOUBLE.fieldOf("Amount").forGetter(EntityAttributeModifier::getValue),
					EntityAttributeModifier.Operation.CODEC.fieldOf("Operation").forGetter(EntityAttributeModifier::getOperation)
				)
				.apply(instance, EntityAttributeModifier::new)
	);
	private final double value;
	private final EntityAttributeModifier.Operation operation;
	private final String name;
	private final UUID uuid;

	public EntityAttributeModifier(String name, double value, EntityAttributeModifier.Operation operation) {
		this(MathHelper.randomUuid(Random.createLocal()), name, value, operation);
	}

	public EntityAttributeModifier(UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) {
		this.uuid = uuid;
		this.name = name;
		this.value = value;
		this.operation = operation;
	}

	public UUID getId() {
		return this.uuid;
	}

	public EntityAttributeModifier.Operation getOperation() {
		return this.operation;
	}

	public double getValue() {
		return this.value;
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
		return this.uuid.hashCode();
	}

	public String toString() {
		return "AttributeModifier{amount=" + this.value + ", operation=" + this.operation + ", name='" + this.name + "', id=" + this.uuid + "}";
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", this.name);
		nbtCompound.putDouble("Amount", this.value);
		nbtCompound.putInt("Operation", this.operation.getId());
		nbtCompound.putUuid("UUID", this.uuid);
		return nbtCompound;
	}

	@Nullable
	public static EntityAttributeModifier fromNbt(NbtCompound nbt) {
		try {
			UUID uUID = nbt.getUuid("UUID");
			EntityAttributeModifier.Operation operation = (EntityAttributeModifier.Operation)EntityAttributeModifier.Operation.ID_TO_VALUE
				.apply(nbt.getInt("Operation"));
			return new EntityAttributeModifier(uUID, nbt.getString("Name"), nbt.getDouble("Amount"), operation);
		} catch (Exception var3) {
			LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
			return null;
		}
	}

	/**
	 * Represents an operation which can be applied to an attribute modifier.
	 */
	public static enum Operation implements StringIdentifiable {
		/**
		 * Adds to the base value of an attribute.
		 */
		ADDITION("addition", 0),
		/**
		 * Multiplies the base value of the attribute.
		 * 
		 * <p>Is applied after addition.
		 */
		MULTIPLY_BASE("multiply_base", 1),
		/**
		 * Multiplies the total value of the attribute.
		 * 
		 * <p>The total value is equal to the sum of all additions and base multiplications applied by an attribute modifier.
		 */
		MULTIPLY_TOTAL("multiply_total", 2);

		public static final IntFunction<EntityAttributeModifier.Operation> ID_TO_VALUE = ValueLists.createIdToValueFunction(
			EntityAttributeModifier.Operation::getId, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		public static final PacketCodec<ByteBuf, EntityAttributeModifier.Operation> PACKET_CODEC = PacketCodecs.indexed(
			ID_TO_VALUE, EntityAttributeModifier.Operation::getId
		);
		public static final Codec<EntityAttributeModifier.Operation> CODEC = StringIdentifiable.createCodec(EntityAttributeModifier.Operation::values);
		private final String name;
		private final int id;

		private Operation(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}

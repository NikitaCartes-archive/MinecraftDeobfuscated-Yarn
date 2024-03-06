package net.minecraft.entity.attribute;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
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

public record EntityAttributeModifier(UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<EntityAttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("uuid").forGetter(EntityAttributeModifier::uuid),
					Codec.STRING.fieldOf("name").forGetter(modifier -> modifier.name),
					Codec.DOUBLE.fieldOf("amount").forGetter(EntityAttributeModifier::value),
					EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(EntityAttributeModifier::operation)
				)
				.apply(instance, EntityAttributeModifier::new)
	);
	public static final Codec<EntityAttributeModifier> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<ByteBuf, EntityAttributeModifier> PACKET_CODEC = PacketCodec.tuple(
		Uuids.PACKET_CODEC,
		EntityAttributeModifier::uuid,
		PacketCodecs.STRING,
		modifier -> modifier.name,
		PacketCodecs.DOUBLE,
		EntityAttributeModifier::value,
		EntityAttributeModifier.Operation.PACKET_CODEC,
		EntityAttributeModifier::operation,
		EntityAttributeModifier::new
	);

	public EntityAttributeModifier(String name, double value, EntityAttributeModifier.Operation operation) {
		this(MathHelper.randomUuid(Random.createLocal()), name, value, operation);
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
		ADD_VALUE("add_value", 0),
		/**
		 * Multiplies the base value of the attribute.
		 * 
		 * <p>Is applied after addition.
		 */
		ADD_MULTIPLIED_BASE("add_multiplied_base", 1),
		/**
		 * Multiplies the total value of the attribute.
		 * 
		 * <p>The total value is equal to the sum of all additions and base multiplications applied by an attribute modifier.
		 */
		ADD_MULTIPLIED_TOTAL("add_multiplied_total", 2);

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

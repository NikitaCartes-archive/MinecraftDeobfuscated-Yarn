package net.minecraft.entity.attribute;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.slf4j.Logger;

public record EntityAttributeModifier(Identifier id, double value, EntityAttributeModifier.Operation operation) {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<EntityAttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("id").forGetter(EntityAttributeModifier::id),
					Codec.DOUBLE.fieldOf("amount").forGetter(EntityAttributeModifier::value),
					EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(EntityAttributeModifier::operation)
				)
				.apply(instance, EntityAttributeModifier::new)
	);
	public static final Codec<EntityAttributeModifier> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<ByteBuf, EntityAttributeModifier> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC,
		EntityAttributeModifier::id,
		PacketCodecs.DOUBLE,
		EntityAttributeModifier::value,
		EntityAttributeModifier.Operation.PACKET_CODEC,
		EntityAttributeModifier::operation,
		EntityAttributeModifier::new
	);

	public NbtCompound toNbt() {
		DataResult<NbtElement> dataResult = CODEC.encode(this, NbtOps.INSTANCE, new NbtCompound());
		return (NbtCompound)dataResult.getOrThrow();
	}

	@Nullable
	public static EntityAttributeModifier fromNbt(NbtCompound nbt) {
		DataResult<EntityAttributeModifier> dataResult = CODEC.parse(NbtOps.INSTANCE, nbt);
		if (dataResult.isSuccess()) {
			return dataResult.getOrThrow();
		} else {
			LOGGER.warn("Unable to create attribute: {}", ((Error)dataResult.error().get()).message());
			return null;
		}
	}

	public boolean idMatches(Identifier id) {
		return id.equals(this.id);
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

		private Operation(final String name, final int id) {
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

package net.minecraft.component.type;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryWrapper;
import org.slf4j.Logger;

public final class NbtComponent {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final NbtComponent DEFAULT = new NbtComponent(new NbtCompound());
	public static final Codec<NbtComponent> CODEC = Codec.withAlternative(NbtCompound.CODEC, StringNbtReader.STRINGIFIED_CODEC)
		.xmap(NbtComponent::new, component -> component.nbt);
	public static final Codec<NbtComponent> CODEC_WITH_ID = CODEC.validate(
		component -> component.getNbt().contains("id", NbtElement.STRING_TYPE)
				? DataResult.success(component)
				: DataResult.error(() -> "Missing id for entity in: " + component)
	);
	@Deprecated
	public static final PacketCodec<ByteBuf, NbtComponent> PACKET_CODEC = PacketCodecs.NBT_COMPOUND.xmap(NbtComponent::new, component -> component.nbt);
	private final NbtCompound nbt;

	private NbtComponent(NbtCompound nbt) {
		this.nbt = nbt;
	}

	public static NbtComponent of(NbtCompound nbt) {
		return new NbtComponent(nbt.copy());
	}

	public static Predicate<ItemStack> createPredicate(ComponentType<NbtComponent> type, NbtCompound nbt) {
		return stack -> {
			NbtComponent nbtComponent = stack.getOrDefault(type, DEFAULT);
			return nbtComponent.matches(nbt);
		};
	}

	public boolean matches(NbtCompound nbt) {
		return NbtHelper.matches(nbt, this.nbt, true);
	}

	public static void set(ComponentType<NbtComponent> type, ItemStack stack, Consumer<NbtCompound> nbtSetter) {
		NbtComponent nbtComponent = stack.getOrDefault(type, DEFAULT).apply(nbtSetter);
		if (nbtComponent.nbt.isEmpty()) {
			stack.remove(type);
		} else {
			stack.set(type, nbtComponent);
		}
	}

	public static void set(ComponentType<NbtComponent> type, ItemStack stack, NbtCompound nbt) {
		if (!nbt.isEmpty()) {
			stack.set(type, of(nbt));
		} else {
			stack.remove(type);
		}
	}

	public NbtComponent apply(Consumer<NbtCompound> nbtConsumer) {
		NbtCompound nbtCompound = this.nbt.copy();
		nbtConsumer.accept(nbtCompound);
		return new NbtComponent(nbtCompound);
	}

	public void applyToEntity(Entity entity) {
		NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
		UUID uUID = entity.getUuid();
		nbtCompound.copyFrom(this.nbt);
		entity.readNbt(nbtCompound);
		entity.setUuid(uUID);
	}

	public boolean applyToBlockEntity(BlockEntity blockEntity, RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound nbtCompound = blockEntity.createComponentlessNbt(registryLookup);
		NbtCompound nbtCompound2 = nbtCompound.copy();
		nbtCompound.copyFrom(this.nbt);
		if (!nbtCompound.equals(nbtCompound2)) {
			try {
				blockEntity.readComponentlessNbt(nbtCompound, registryLookup);
				blockEntity.markDirty();
				return true;
			} catch (Exception var8) {
				LOGGER.warn("Failed to apply custom data to block entity at {}", blockEntity.getPos(), var8);

				try {
					blockEntity.readComponentlessNbt(nbtCompound2, registryLookup);
				} catch (Exception var7) {
					LOGGER.warn("Failed to rollback block entity at {} after failure", blockEntity.getPos(), var7);
				}
			}
		}

		return false;
	}

	public <T> DataResult<NbtComponent> with(DynamicOps<NbtElement> ops, MapEncoder<T> encoder, T value) {
		return encoder.encode(value, ops, ops.mapBuilder()).build(this.nbt).map(nbt -> new NbtComponent((NbtCompound)nbt));
	}

	public <T> DataResult<T> get(MapDecoder<T> decoder) {
		return this.get(NbtOps.INSTANCE, decoder);
	}

	public <T> DataResult<T> get(DynamicOps<NbtElement> ops, MapDecoder<T> decoder) {
		MapLike<NbtElement> mapLike = ops.getMap(this.nbt).getOrThrow();
		return decoder.decode(ops, mapLike);
	}

	public int getSize() {
		return this.nbt.getSize();
	}

	public boolean isEmpty() {
		return this.nbt.isEmpty();
	}

	public NbtCompound copyNbt() {
		return this.nbt.copy();
	}

	public boolean contains(String key) {
		return this.nbt.contains(key);
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			return o instanceof NbtComponent nbtComponent ? this.nbt.equals(nbtComponent.nbt) : false;
		}
	}

	public int hashCode() {
		return this.nbt.hashCode();
	}

	public String toString() {
		return this.nbt.toString();
	}

	@Deprecated
	public NbtCompound getNbt() {
		return this.nbt;
	}
}

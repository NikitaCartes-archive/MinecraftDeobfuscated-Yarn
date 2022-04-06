package net.minecraft.entity.data;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf buf, T value);

	T read(PacketByteBuf buf);

	default TrackedData<T> create(int id) {
		return new TrackedData<>(id, this);
	}

	T copy(T value);

	static <T> TrackedDataHandler<T> of(BiConsumer<PacketByteBuf, T> writer, Function<PacketByteBuf, T> reader) {
		return new TrackedDataHandler.ImmutableHandler<T>() {
			@Override
			public void write(PacketByteBuf buf, T value) {
				writer.accept(buf, value);
			}

			@Override
			public T read(PacketByteBuf buf) {
				return (T)reader.apply(buf);
			}
		};
	}

	static <T> TrackedDataHandler<Optional<T>> ofOptional(BiConsumer<PacketByteBuf, T> writer, Function<PacketByteBuf, T> reader) {
		return new TrackedDataHandler.ImmutableHandler<Optional<T>>() {
			public void write(PacketByteBuf packetByteBuf, Optional<T> optional) {
				if (optional.isPresent()) {
					packetByteBuf.writeBoolean(true);
					writer.accept(packetByteBuf, optional.get());
				} else {
					packetByteBuf.writeBoolean(false);
				}
			}

			public Optional<T> read(PacketByteBuf packetByteBuf) {
				return packetByteBuf.readBoolean() ? Optional.of(reader.apply(packetByteBuf)) : Optional.empty();
			}
		};
	}

	static <T extends Enum<T>> TrackedDataHandler<T> ofEnum(Class<T> enum_) {
		return of(PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(enum_));
	}

	static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry) {
		return of((buf, value) -> buf.writeRegistryValue(registry, (T)value), buf -> buf.readRegistryValue(registry));
	}

	public interface ImmutableHandler<T> extends TrackedDataHandler<T> {
		@Override
		default T copy(T value) {
			return value;
		}
	}
}

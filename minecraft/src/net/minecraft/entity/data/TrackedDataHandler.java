package net.minecraft.entity.data;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface TrackedDataHandler<T> {
	void write(PacketByteBuf buf, T value);

	T read(PacketByteBuf buf);

	default TrackedData<T> create(int id) {
		return new TrackedData<>(id, this);
	}

	T copy(T value);

	static <T> TrackedDataHandler<T> of(PacketByteBuf.PacketWriter<T> writer, PacketByteBuf.PacketReader<T> reader) {
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

	static <T> TrackedDataHandler<Optional<T>> ofOptional(PacketByteBuf.PacketWriter<T> writer, PacketByteBuf.PacketReader<T> reader) {
		return of(writer.asOptional(), reader.asOptional());
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

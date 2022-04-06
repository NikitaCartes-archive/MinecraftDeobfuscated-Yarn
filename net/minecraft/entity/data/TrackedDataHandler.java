/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface TrackedDataHandler<T> {
    public void write(PacketByteBuf var1, T var2);

    public T read(PacketByteBuf var1);

    default public TrackedData<T> create(int id) {
        return new TrackedData(id, this);
    }

    public T copy(T var1);

    public static <T> TrackedDataHandler<T> of(final BiConsumer<PacketByteBuf, T> writer, final Function<PacketByteBuf, T> reader) {
        return new ImmutableHandler<T>(){

            @Override
            public void write(PacketByteBuf buf, T value) {
                writer.accept(buf, value);
            }

            @Override
            public T read(PacketByteBuf buf) {
                return reader.apply(buf);
            }
        };
    }

    public static <T> TrackedDataHandler<Optional<T>> ofOptional(final BiConsumer<PacketByteBuf, T> writer, final Function<PacketByteBuf, T> reader) {
        return new ImmutableHandler<Optional<T>>(){

            @Override
            public void write(PacketByteBuf packetByteBuf, Optional<T> optional) {
                if (optional.isPresent()) {
                    packetByteBuf.writeBoolean(true);
                    writer.accept(packetByteBuf, optional.get());
                } else {
                    packetByteBuf.writeBoolean(false);
                }
            }

            @Override
            public Optional<T> read(PacketByteBuf packetByteBuf) {
                if (packetByteBuf.readBoolean()) {
                    return Optional.of(reader.apply(packetByteBuf));
                }
                return Optional.empty();
            }

            @Override
            public /* synthetic */ Object read(PacketByteBuf buf) {
                return this.read(buf);
            }
        };
    }

    public static <T extends Enum<T>> TrackedDataHandler<T> ofEnum(Class<T> enum_) {
        return TrackedDataHandler.of(PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(enum_));
    }

    public static <T> TrackedDataHandler<T> of(IndexedIterable<T> registry) {
        return TrackedDataHandler.of((buf, value) -> buf.writeRegistryValue(registry, value), buf -> buf.readRegistryValue(registry));
    }

    public static interface ImmutableHandler<T>
    extends TrackedDataHandler<T> {
        @Override
        default public T copy(T value) {
            return value;
        }
    }
}


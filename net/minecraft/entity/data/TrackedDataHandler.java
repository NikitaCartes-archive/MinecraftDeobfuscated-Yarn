/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.data;

import java.util.Optional;
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

    public static <T> TrackedDataHandler<T> of(final PacketByteBuf.PacketWriter<T> writer, final PacketByteBuf.PacketReader<T> reader) {
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

    public static <T> TrackedDataHandler<Optional<T>> ofOptional(PacketByteBuf.PacketWriter<T> writer, PacketByteBuf.PacketReader<T> reader) {
        return TrackedDataHandler.of(writer.asOptional(), reader.asOptional());
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


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.jetbrains.annotations.Nullable;

public class ArrayPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final Function<NbtCompound, T> valueDeserializer;
    private final int indexBits;
    private int size;

    public ArrayPalette(IdList<T> idList, int integer, PaletteResizeListener<T> resizeListener, Function<NbtCompound, T> valueDeserializer) {
        this.idList = idList;
        this.array = new Object[1 << integer];
        this.indexBits = integer;
        this.resizeListener = resizeListener;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public int getIndex(T object) {
        int i;
        for (i = 0; i < this.size; ++i) {
            if (this.array[i] != object) continue;
            return i;
        }
        if ((i = this.size++) < this.array.length) {
            this.array[i] = object;
            return i;
        }
        return this.resizeListener.onResize(this.indexBits + 1, object);
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (!predicate.test(this.array[i])) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public T getByIndex(int index) {
        if (index >= 0 && index < this.size) {
            return this.array[index];
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf buf) {
        this.size = buf.readVarInt();
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = this.idList.get(buf.readVarInt());
        }
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        buf.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            buf.writeVarInt(this.idList.getRawId(this.array[i]));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getSize());
        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getRawId(this.array[j]));
        }
        return i;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void readNbt(NbtList tag) {
        for (int i = 0; i < tag.size(); ++i) {
            this.array[i] = this.valueDeserializer.apply(tag.getCompound(i));
        }
        this.size = tag.size();
    }
}


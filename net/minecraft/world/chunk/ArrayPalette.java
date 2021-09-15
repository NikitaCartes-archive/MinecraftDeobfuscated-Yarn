/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.class_6558;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;

public class ArrayPalette<T>
implements Palette<T> {
    private final IndexedIterable<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final int indexBits;
    private int size;

    public ArrayPalette(IndexedIterable<T> indexedIterable, int integer, PaletteResizeListener<T> resizeListener) {
        this.idList = indexedIterable;
        this.array = new Object[1 << integer];
        this.indexBits = integer;
        this.resizeListener = resizeListener;
    }

    public static <A> Palette<A> method_38295(int i, IndexedIterable<A> indexedIterable, PaletteResizeListener<A> paletteResizeListener) {
        return new ArrayPalette<A>(indexedIterable, i, paletteResizeListener);
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
    public T getByIndex(int index) {
        if (index >= 0 && index < this.size) {
            return this.array[index];
        }
        throw new class_6558(index);
    }

    @Override
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
        int i = PacketByteBuf.getVarIntLength(this.getIndexBits());
        for (int j = 0; j < this.getIndexBits(); ++j) {
            i += PacketByteBuf.getVarIntLength(this.idList.getRawId(this.array[j]));
        }
        return i;
    }

    @Override
    public int getIndexBits() {
        return this.size;
    }
}


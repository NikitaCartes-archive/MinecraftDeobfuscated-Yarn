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

public class IdListPalette<T>
implements Palette<T> {
    private final IndexedIterable<T> idList;

    public IdListPalette(IndexedIterable<T> indexedIterable) {
        this.idList = indexedIterable;
    }

    public static <A> Palette<A> method_38286(int i, IndexedIterable<A> indexedIterable, PaletteResizeListener<A> paletteResizeListener) {
        return new IdListPalette<A>(indexedIterable);
    }

    @Override
    public int getIndex(T object) {
        int i = this.idList.getRawId(object);
        return i == -1 ? 0 : i;
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T getByIndex(int index) {
        T object = this.idList.get(index);
        if (object == null) {
            throw new class_6558(index);
        }
        return object;
    }

    @Override
    public void fromPacket(PacketByteBuf buf) {
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
    }

    @Override
    public int getPacketSize() {
        return PacketByteBuf.getVarIntLength(0);
    }

    @Override
    public int getIndexBits() {
        return this.idList.size();
    }
}


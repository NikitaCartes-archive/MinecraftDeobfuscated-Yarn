/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.PaletteResizeListener;

public interface Palette<T> {
    public int getIndex(T var1);

    public boolean accepts(Predicate<T> var1);

    public T getByIndex(int var1);

    public void fromPacket(PacketByteBuf var1);

    public void toPacket(PacketByteBuf var1);

    public int getPacketSize();

    public int getIndexBits();

    public static interface class_6559 {
        public <A> Palette<A> create(int var1, IndexedIterable<A> var2, PaletteResizeListener<A> var3);
    }
}


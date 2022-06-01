/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.PalettedContainer;

public interface class_7522<T> {
    public T get(int var1, int var2, int var3);

    public void method_39793(Consumer<T> var1);

    public void writePacket(PacketByteBuf var1);

    public int getPacketSize();

    public boolean hasAny(Predicate<T> var1);

    public void count(PalettedContainer.Counter<T> var1);

    public PalettedContainer<T> method_44350();

    public Serialized<T> method_44345(IndexedIterable<T> var1, PalettedContainer.PaletteProvider var2);

    public static interface class_7523<T, C extends class_7522<T>> {
        public DataResult<C> read(IndexedIterable<T> var1, PalettedContainer.PaletteProvider var2, Serialized<T> var3);
    }

    public record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage) {
    }
}


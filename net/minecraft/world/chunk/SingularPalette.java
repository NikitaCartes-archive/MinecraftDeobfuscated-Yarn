/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

/**
 * A palette that only holds a unique entry. Useful for void chunks or a
 * single biome.
 */
public class SingularPalette<T>
implements Palette<T> {
    private final IndexedIterable<T> idList;
    @Nullable
    private T entry;
    private final PaletteResizeListener<T> listener;

    public SingularPalette(IndexedIterable<T> idList, PaletteResizeListener<T> listener, List<T> list) {
        this.idList = idList;
        this.listener = listener;
        if (list.size() > 0) {
            Validate.isTrue(list.size() <= 1, "Can't initialize SingleValuePalette with %d values.", list.size());
            this.entry = list.get(0);
        }
    }

    /**
     * Creates a singular pallete. Used as method reference to create factory.
     * 
     * @param bitSize {@code 0}, as this palette has only 2<sup>0</sup>=1 entry
     */
    public static <A> Palette<A> create(int bitSize, IndexedIterable<A> idList, PaletteResizeListener<A> listener, List<A> list) {
        return new SingularPalette<A>(idList, listener, list);
    }

    @Override
    public int index(T object) {
        if (this.entry == null || this.entry == object) {
            this.entry = object;
            return 0;
        }
        return this.listener.onResize(1, object);
    }

    @Override
    public boolean hasAny(Predicate<T> predicate) {
        if (this.entry == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        return predicate.test(this.entry);
    }

    @Override
    public T get(int id) {
        if (this.entry == null || id != 0) {
            throw new IllegalStateException("Missing Palette entry for id " + id + ".");
        }
        return this.entry;
    }

    @Override
    public void readPacket(PacketByteBuf buf) {
        this.entry = this.idList.get(buf.readVarInt());
    }

    @Override
    public void writePacket(PacketByteBuf buf) {
        if (this.entry == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        buf.writeVarInt(this.idList.getRawId(this.entry));
    }

    @Override
    public int getPacketSize() {
        if (this.entry == null) {
            throw new IllegalStateException("Use of an uninitialized palette");
        }
        return PacketByteBuf.getVarIntLength(this.idList.getRawId(this.entry));
    }

    @Override
    public int getSize() {
        return 1;
    }
}


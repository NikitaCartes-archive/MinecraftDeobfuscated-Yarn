/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

public class ArrayPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final T[] array;
    private final PaletteResizeListener<T> resizeListener;
    private final Function<CompoundTag, T> valueDeserializer;
    private final int indexBits;
    private int size;

    public ArrayPalette(IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<CompoundTag, T> function) {
        this.idList = idList;
        this.array = new Object[1 << i];
        this.indexBits = i;
        this.resizeListener = paletteResizeListener;
        this.valueDeserializer = function;
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
    public boolean accepts(T object) {
        return ArrayUtils.contains(this.array, object);
    }

    @Override
    @Nullable
    public T getByIndex(int i) {
        if (i >= 0 && i < this.size) {
            return this.array[i];
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf packetByteBuf) {
        this.size = packetByteBuf.readVarInt();
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = this.idList.get(packetByteBuf.readVarInt());
        }
    }

    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            packetByteBuf.writeVarInt(this.idList.getId(this.array[i]));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getSize());
        for (int j = 0; j < this.getSize(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.array[j]));
        }
        return i;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void fromTag(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            this.array[i] = this.valueDeserializer.apply(listTag.getCompoundTag(i));
        }
        this.size = listTag.size();
    }
}


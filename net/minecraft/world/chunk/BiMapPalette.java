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
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PaletteResizeListener;
import org.jetbrains.annotations.Nullable;

public class BiMapPalette<T>
implements Palette<T> {
    private final IdList<T> idList;
    private final Int2ObjectBiMap<T> map;
    private final PaletteResizeListener<T> resizeHandler;
    private final Function<CompoundTag, T> elementDeserializer;
    private final Function<T, CompoundTag> elementSerializer;
    private final int indexBits;

    public BiMapPalette(IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<CompoundTag, T> function, Function<T, CompoundTag> function2) {
        this.idList = idList;
        this.indexBits = i;
        this.resizeHandler = paletteResizeListener;
        this.elementDeserializer = function;
        this.elementSerializer = function2;
        this.map = new Int2ObjectBiMap(1 << i);
    }

    @Override
    public int getIndex(T object) {
        int i = this.map.getId(object);
        if (i == -1 && (i = this.map.add(object)) >= 1 << this.indexBits) {
            i = this.resizeHandler.onResize(this.indexBits + 1, object);
        }
        return i;
    }

    @Override
    public boolean accepts(T object) {
        return this.map.getId(object) != -1;
    }

    @Override
    @Nullable
    public T getByIndex(int i) {
        return this.map.get(i);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf packetByteBuf) {
        this.map.clear();
        int i = packetByteBuf.readVarInt();
        for (int j = 0; j < i; ++j) {
            this.map.add(this.idList.get(packetByteBuf.readVarInt()));
        }
    }

    @Override
    public void toPacket(PacketByteBuf packetByteBuf) {
        int i = this.getIndexBits();
        packetByteBuf.writeVarInt(i);
        for (int j = 0; j < i; ++j) {
            packetByteBuf.writeVarInt(this.idList.getId(this.map.get(j)));
        }
    }

    @Override
    public int getPacketSize() {
        int i = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());
        for (int j = 0; j < this.getIndexBits(); ++j) {
            i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.map.get(j)));
        }
        return i;
    }

    public int getIndexBits() {
        return this.map.size();
    }

    @Override
    public void fromTag(ListTag listTag) {
        this.map.clear();
        for (int i = 0; i < listTag.size(); ++i) {
            this.map.add(this.elementDeserializer.apply(listTag.getCompound(i)));
        }
    }

    public void toTag(ListTag listTag) {
        for (int i = 0; i < this.getIndexBits(); ++i) {
            listTag.add(this.elementSerializer.apply(this.map.get(i)));
        }
    }
}


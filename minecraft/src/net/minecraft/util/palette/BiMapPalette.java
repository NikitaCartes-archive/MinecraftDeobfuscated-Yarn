package net.minecraft.util.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PacketByteBuf;

public class BiMapPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final Int2ObjectBiMap<T> map;
	private final PaletteResizeHandler<T> resizeHandler;
	private final Function<CompoundTag, T> elementDeserializer;
	private final Function<T, CompoundTag> elementSerializer;
	private final int indexBits;

	public BiMapPalette(
		IdList<T> idList, int i, PaletteResizeHandler<T> paletteResizeHandler, Function<CompoundTag, T> function, Function<T, CompoundTag> function2
	) {
		this.idList = idList;
		this.indexBits = i;
		this.resizeHandler = paletteResizeHandler;
		this.elementDeserializer = function;
		this.elementSerializer = function2;
		this.map = new Int2ObjectBiMap<>(1 << i);
	}

	@Override
	public int getIndex(T object) {
		int i = this.map.getId(object);
		if (i == -1) {
			i = this.map.add(object);
			if (i >= 1 << this.indexBits) {
				i = this.resizeHandler.resizePaletteAndGetIndex(this.indexBits + 1, object);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public T getByIndex(int i) {
		return this.map.getInt(i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void fromPacket(PacketByteBuf packetByteBuf) {
		this.map.clear();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.map.add(this.idList.getInt(packetByteBuf.readVarInt()));
		}
	}

	@Override
	public void toPacket(PacketByteBuf packetByteBuf) {
		int i = this.getIndexBits();
		packetByteBuf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			packetByteBuf.writeVarInt(this.idList.getId(this.map.getInt(j)));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());

		for (int j = 0; j < this.getIndexBits(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.idList.getId(this.map.getInt(j)));
		}

		return i;
	}

	public int getIndexBits() {
		return this.map.size();
	}

	@Override
	public void fromTag(ListTag listTag) {
		this.map.clear();

		for (int i = 0; i < listTag.size(); i++) {
			this.map.add((T)this.elementDeserializer.apply(listTag.getCompoundTag(i)));
		}
	}

	public void toTag(ListTag listTag) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			listTag.add(this.elementSerializer.apply(this.map.getInt(i)));
		}
	}
}

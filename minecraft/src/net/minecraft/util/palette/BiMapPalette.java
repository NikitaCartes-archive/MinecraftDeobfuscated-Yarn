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
	private final IdList<T> field_12821;
	private final Int2ObjectBiMap<T> field_12824;
	private final PaletteResizeListener<T> field_12825;
	private final Function<CompoundTag, T> elementDeserializer;
	private final Function<T, CompoundTag> elementSerializer;
	private final int indexBits;

	public BiMapPalette(
		IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<CompoundTag, T> function, Function<T, CompoundTag> function2
	) {
		this.field_12821 = idList;
		this.indexBits = i;
		this.field_12825 = paletteResizeListener;
		this.elementDeserializer = function;
		this.elementSerializer = function2;
		this.field_12824 = new Int2ObjectBiMap<>(1 << i);
	}

	@Override
	public int getIndex(T object) {
		int i = this.field_12824.getId(object);
		if (i == -1) {
			i = this.field_12824.add(object);
			if (i >= 1 << this.indexBits) {
				i = this.field_12825.onResize(this.indexBits + 1, object);
			}
		}

		return i;
	}

	@Nullable
	@Override
	public T getByIndex(int i) {
		return this.field_12824.get(i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(PacketByteBuf packetByteBuf) {
		this.field_12824.clear();
		int i = packetByteBuf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.field_12824.add(this.field_12821.get(packetByteBuf.readVarInt()));
		}
	}

	@Override
	public void method_12287(PacketByteBuf packetByteBuf) {
		int i = this.getIndexBits();
		packetByteBuf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			packetByteBuf.writeVarInt(this.field_12821.getId(this.field_12824.get(j)));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.getIndexBits());

		for (int j = 0; j < this.getIndexBits(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_12821.getId(this.field_12824.get(j)));
		}

		return i;
	}

	public int getIndexBits() {
		return this.field_12824.size();
	}

	@Override
	public void method_12286(ListTag listTag) {
		this.field_12824.clear();

		for (int i = 0; i < listTag.size(); i++) {
			this.field_12824.add((T)this.elementDeserializer.apply(listTag.getCompoundTag(i)));
		}
	}

	public void method_12196(ListTag listTag) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			listTag.add(this.elementSerializer.apply(this.field_12824.get(i)));
		}
	}
}

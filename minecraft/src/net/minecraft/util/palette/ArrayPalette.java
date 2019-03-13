package net.minecraft.util.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.IdList;
import net.minecraft.util.PacketByteBuf;

public class ArrayPalette<T> implements Palette<T> {
	private final IdList<T> field_12900;
	private final T[] field_12904;
	private final PaletteResizeListener<T> field_12905;
	private final Function<CompoundTag, T> valueDeserializer;
	private final int indexBits;
	private int size;

	public ArrayPalette(IdList<T> idList, int i, PaletteResizeListener<T> paletteResizeListener, Function<CompoundTag, T> function) {
		this.field_12900 = idList;
		this.field_12904 = (T[])(new Object[1 << i]);
		this.indexBits = i;
		this.field_12905 = paletteResizeListener;
		this.valueDeserializer = function;
	}

	@Override
	public int getIndex(T object) {
		for (int i = 0; i < this.size; i++) {
			if (this.field_12904[i] == object) {
				return i;
			}
		}

		int ix = this.size;
		if (ix < this.field_12904.length) {
			this.field_12904[ix] = object;
			this.size++;
			return ix;
		} else {
			return this.field_12905.onResize(this.indexBits + 1, object);
		}
	}

	@Nullable
	@Override
	public T getByIndex(int i) {
		return i >= 0 && i < this.size ? this.field_12904[i] : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_12289(PacketByteBuf packetByteBuf) {
		this.size = packetByteBuf.readVarInt();

		for (int i = 0; i < this.size; i++) {
			this.field_12904[i] = this.field_12900.get(packetByteBuf.readVarInt());
		}
	}

	@Override
	public void method_12287(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(this.size);

		for (int i = 0; i < this.size; i++) {
			packetByteBuf.writeVarInt(this.field_12900.getId(this.field_12904[i]));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntSizeBytes(this.method_12282());

		for (int j = 0; j < this.method_12282(); j++) {
			i += PacketByteBuf.getVarIntSizeBytes(this.field_12900.getId(this.field_12904[j]));
		}

		return i;
	}

	public int method_12282() {
		return this.size;
	}

	@Override
	public void method_12286(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			this.field_12904[i] = (T)this.valueDeserializer.apply(listTag.getCompoundTag(i));
		}

		this.size = listTag.size();
	}
}

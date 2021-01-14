package net.minecraft.world.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;

public class ArrayPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final T[] array;
	private final PaletteResizeListener<T> resizeListener;
	private final Function<NbtCompound, T> valueDeserializer;
	private final int indexBits;
	private int size;

	public ArrayPalette(IdList<T> idList, int integer, PaletteResizeListener<T> resizeListener, Function<NbtCompound, T> valueDeserializer) {
		this.idList = idList;
		this.array = (T[])(new Object[1 << integer]);
		this.indexBits = integer;
		this.resizeListener = resizeListener;
		this.valueDeserializer = valueDeserializer;
	}

	@Override
	public int getIndex(T object) {
		for (int i = 0; i < this.size; i++) {
			if (this.array[i] == object) {
				return i;
			}
		}

		int ix = this.size;
		if (ix < this.array.length) {
			this.array[ix] = object;
			this.size++;
			return ix;
		} else {
			return this.resizeListener.onResize(this.indexBits + 1, object);
		}
	}

	@Override
	public boolean accepts(Predicate<T> predicate) {
		for (int i = 0; i < this.size; i++) {
			if (predicate.test(this.array[i])) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public T getByIndex(int index) {
		return index >= 0 && index < this.size ? this.array[index] : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void fromPacket(PacketByteBuf buf) {
		this.size = buf.readVarInt();

		for (int i = 0; i < this.size; i++) {
			this.array[i] = this.idList.get(buf.readVarInt());
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		buf.writeVarInt(this.size);

		for (int i = 0; i < this.size; i++) {
			buf.writeVarInt(this.idList.getRawId(this.array[i]));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntLength(this.getSize());

		for (int j = 0; j < this.getSize(); j++) {
			i += PacketByteBuf.getVarIntLength(this.idList.getRawId(this.array[j]));
		}

		return i;
	}

	public int getSize() {
		return this.size;
	}

	@Override
	public void readNbt(NbtList nbt) {
		for (int i = 0; i < nbt.size(); i++) {
			this.array[i] = (T)this.valueDeserializer.apply(nbt.getCompound(i));
		}

		this.size = nbt.size();
	}
}

package net.minecraft.world.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.Int2ObjectBiMap;

public class BiMapPalette<T> implements Palette<T> {
	private final IdList<T> idList;
	private final Int2ObjectBiMap<T> map;
	private final PaletteResizeListener<T> resizeHandler;
	private final Function<NbtCompound, T> elementDeserializer;
	private final Function<T, NbtCompound> elementSerializer;
	private final int indexBits;

	public BiMapPalette(
		IdList<T> idList,
		int indexBits,
		PaletteResizeListener<T> resizeHandler,
		Function<NbtCompound, T> elementDeserializer,
		Function<T, NbtCompound> elementSerializer
	) {
		this.idList = idList;
		this.indexBits = indexBits;
		this.resizeHandler = resizeHandler;
		this.elementDeserializer = elementDeserializer;
		this.elementSerializer = elementSerializer;
		this.map = new Int2ObjectBiMap<>(1 << indexBits);
	}

	@Override
	public int getIndex(T object) {
		int i = this.map.getRawId(object);
		if (i == -1) {
			i = this.map.add(object);
			if (i >= 1 << this.indexBits) {
				i = this.resizeHandler.onResize(this.indexBits + 1, object);
			}
		}

		return i;
	}

	@Override
	public boolean accepts(Predicate<T> predicate) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			if (predicate.test(this.map.get(i))) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public T getByIndex(int index) {
		return this.map.get(index);
	}

	@Override
	public void fromPacket(PacketByteBuf buf) {
		this.map.clear();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.map.add(this.idList.get(buf.readVarInt()));
		}
	}

	@Override
	public void toPacket(PacketByteBuf buf) {
		int i = this.getIndexBits();
		buf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(this.idList.getRawId(this.map.get(j)));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntLength(this.getIndexBits());

		for (int j = 0; j < this.getIndexBits(); j++) {
			i += PacketByteBuf.getVarIntLength(this.idList.getRawId(this.map.get(j)));
		}

		return i;
	}

	@Override
	public int getIndexBits() {
		return this.map.size();
	}

	@Override
	public void readNbt(NbtList nbt) {
		this.map.clear();

		for (int i = 0; i < nbt.size(); i++) {
			this.map.add((T)this.elementDeserializer.apply(nbt.getCompound(i)));
		}
	}

	public void writeNbt(NbtList nbt) {
		for (int i = 0; i < this.getIndexBits(); i++) {
			nbt.add((NbtElement)this.elementSerializer.apply(this.map.get(i)));
		}
	}
}

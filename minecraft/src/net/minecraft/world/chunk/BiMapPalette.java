package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_6558;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;

public class BiMapPalette<T> implements Palette<T> {
	private final IndexedIterable<T> idList;
	private final Int2ObjectBiMap<T> map;
	private final PaletteResizeListener<T> resizeHandler;
	private final int indexBits;

	public BiMapPalette(IndexedIterable<T> indexedIterable, int i, PaletteResizeListener<T> paletteResizeListener, List<T> list) {
		this(indexedIterable, i, paletteResizeListener);
		list.forEach(this.map::add);
	}

	public BiMapPalette(IndexedIterable<T> indexedIterable, int indexBits, PaletteResizeListener<T> resizeHandler) {
		this.idList = indexedIterable;
		this.indexBits = indexBits;
		this.resizeHandler = resizeHandler;
		this.map = Int2ObjectBiMap.method_37913(1 << indexBits);
	}

	public static <A> Palette<A> method_38287(int i, IndexedIterable<A> indexedIterable, PaletteResizeListener<A> paletteResizeListener) {
		return new BiMapPalette<>(indexedIterable, i, paletteResizeListener);
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

	@Override
	public T getByIndex(int index) {
		T object = this.map.get(index);
		if (object == null) {
			throw new class_6558(index);
		} else {
			return object;
		}
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

	public List<T> method_38288() {
		ArrayList<T> arrayList = new ArrayList();
		this.map.iterator().forEachRemaining(arrayList::add);
		return arrayList;
	}

	@Override
	public int getIndexBits() {
		return this.map.size();
	}
}

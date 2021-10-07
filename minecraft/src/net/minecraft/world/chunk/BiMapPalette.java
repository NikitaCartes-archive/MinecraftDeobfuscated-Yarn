package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;

/**
 * A palette backed by a bidirectional hash table.
 */
public class BiMapPalette<T> implements Palette<T> {
	private final IndexedIterable<T> idList;
	private final Int2ObjectBiMap<T> map;
	private final PaletteResizeListener<T> listener;
	private final int indexBits;

	public BiMapPalette(IndexedIterable<T> idList, int bits, PaletteResizeListener<T> listener, List<T> entries) {
		this(idList, bits, listener);
		entries.forEach(this.map::add);
	}

	public BiMapPalette(IndexedIterable<T> idList, int indexBits, PaletteResizeListener<T> listener) {
		this.idList = idList;
		this.indexBits = indexBits;
		this.listener = listener;
		this.map = Int2ObjectBiMap.create(1 << indexBits);
	}

	public static <A> Palette<A> create(int bits, IndexedIterable<A> idList, PaletteResizeListener<A> listener, List<A> list) {
		return new BiMapPalette<>(idList, bits, listener, list);
	}

	@Override
	public int index(T object) {
		int i = this.map.getRawId(object);
		if (i == -1) {
			i = this.map.add(object);
			if (i >= 1 << this.indexBits) {
				i = this.listener.onResize(this.indexBits + 1, object);
			}
		}

		return i;
	}

	@Override
	public boolean hasAny(Predicate<T> predicate) {
		for (int i = 0; i < this.getSize(); i++) {
			if (predicate.test(this.map.get(i))) {
				return true;
			}
		}

		return false;
	}

	@Override
	public T get(int id) {
		T object = this.map.get(id);
		if (object == null) {
			throw new EntryMissingException(id);
		} else {
			return object;
		}
	}

	@Override
	public void readPacket(PacketByteBuf buf) {
		this.map.clear();
		int i = buf.readVarInt();

		for (int j = 0; j < i; j++) {
			this.map.add(this.idList.get(buf.readVarInt()));
		}
	}

	@Override
	public void writePacket(PacketByteBuf buf) {
		int i = this.getSize();
		buf.writeVarInt(i);

		for (int j = 0; j < i; j++) {
			buf.writeVarInt(this.idList.getRawId(this.map.get(j)));
		}
	}

	@Override
	public int getPacketSize() {
		int i = PacketByteBuf.getVarIntLength(this.getSize());

		for (int j = 0; j < this.getSize(); j++) {
			i += PacketByteBuf.getVarIntLength(this.idList.getRawId(this.map.get(j)));
		}

		return i;
	}

	public List<T> getElements() {
		ArrayList<T> arrayList = new ArrayList();
		this.map.iterator().forEachRemaining(arrayList::add);
		return arrayList;
	}

	@Override
	public int getSize() {
		return this.map.size();
	}
}

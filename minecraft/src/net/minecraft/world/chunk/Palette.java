package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface Palette<T> {
	int getIndex(T object);

	boolean accepts(Predicate<T> predicate);

	T getByIndex(int index);

	void fromPacket(PacketByteBuf buf);

	void toPacket(PacketByteBuf buf);

	int getPacketSize();

	int getIndexBits();

	public interface class_6559 {
		<A> Palette<A> create(int i, IndexedIterable<A> indexedIterable, PaletteResizeListener<A> paletteResizeListener);
	}
}

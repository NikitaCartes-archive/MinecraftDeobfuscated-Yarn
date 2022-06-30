package net.minecraft.world.chunk;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface ReadableContainer<T> {
	T get(int x, int y, int z);

	void forEachValue(Consumer<T> action);

	/**
	 * Writes this container to the packet byte buffer.
	 * 
	 * @param buf the packet byte buffer
	 */
	void writePacket(PacketByteBuf buf);

	int getPacketSize();

	/**
	 * {@return {@code true} if any object in this container's palette matches
	 * this predicate}
	 */
	boolean hasAny(Predicate<T> predicate);

	void count(PalettedContainer.Counter<T> counter);

	PalettedContainer<T> slice();

	ReadableContainer.Serialized<T> serialize(IndexedIterable<T> idList, PalettedContainer.PaletteProvider paletteProvider);

	public interface Reader<T, C extends ReadableContainer<T>> {
		DataResult<C> read(IndexedIterable<T> idList, PalettedContainer.PaletteProvider paletteProvider, ReadableContainer.Serialized<T> serialize);
	}

	/**
	 * The storage form of the paletted container in the {@linkplain
	 * PalettedContainer#createCodec codec}. The {@code palette} is the entries
	 * in the palette, but the interpretation of data depends on the palette
	 * provider specified for the codec.
	 * 
	 * @see PalettedContainer#createCodec
	 */
	public static record Serialized<T>(List<T> paletteEntries, Optional<LongStream> storage) {
	}
}

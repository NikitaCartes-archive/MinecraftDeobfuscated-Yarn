package net.minecraft;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.PalettedContainer;

public interface class_7522<T> {
	T get(int i, int j, int k);

	void method_39793(Consumer<T> consumer);

	void writePacket(PacketByteBuf packetByteBuf);

	int getPacketSize();

	boolean hasAny(Predicate<T> predicate);

	void count(PalettedContainer.Counter<T> counter);

	PalettedContainer<T> method_44350();

	class_7522.Serialized<T> method_44345(IndexedIterable<T> indexedIterable, PalettedContainer.PaletteProvider paletteProvider);

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

	public interface class_7523<T, C extends class_7522<T>> {
		DataResult<C> read(IndexedIterable<T> indexedIterable, PalettedContainer.PaletteProvider paletteProvider, class_7522.Serialized<T> serialized);
	}
}

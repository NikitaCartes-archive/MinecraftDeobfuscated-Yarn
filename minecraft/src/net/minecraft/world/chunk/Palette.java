package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

/**
 * A palette maps objects from and to small integer IDs that uses less
 * number of bits to make storage smaller.
 * 
 * <p>While the objects palettes handle are already represented by integer
 * IDs, shrinking IDs in cases where only a few appear can further reduce
 * storage space and network traffic volume.
 * 
 * @see PalettedContainer
 */
public interface Palette<T> {
	/**
	 * {@return the ID of an object in this palette}
	 * 
	 * <p>If the object does not yet exist in this palette, this palette will
	 * register the object. If the palette is too small to include this object,
	 * a {@linkplain PaletteResizeListener resize listener} will be called and
	 * this palette may be discarded.
	 * 
	 * @param object the object to look up
	 */
	int index(T object);

	/**
	 * {@return {@code true} if any entry in this palette passes the {@code
	 * predicate}}
	 */
	boolean hasAny(Predicate<T> predicate);

	/**
	 * {@return the object associated with the given {@code id}}
	 * 
	 * @throws EntryMissingException if this ID does not exist in this palette
	 * 
	 * @param id the ID to look up
	 */
	T get(int id);

	/**
	 * Initializes this palette from the {@code buf}. Clears the preexisting
	 * data in this palette.
	 * 
	 * @param buf the packet byte buffer
	 */
	void readPacket(PacketByteBuf buf);

	/**
	 * Writes this palette to the {@code buf}.
	 * 
	 * @param buf the packet byte buffer
	 */
	void writePacket(PacketByteBuf buf);

	/**
	 * {@return the serialized size of this palette in a byte buf, in bytes}
	 */
	int getPacketSize();

	/**
	 * {@return the size of the palette}
	 */
	int getSize();

	Palette<T> copy();

	/**
	 * An interface for easy creation of palettes.
	 */
	public interface Factory {
		/**
		 * Creates a palette.
		 * 
		 * @return the created new palette
		 * 
		 * @param listener the resize listener, called when this palette runs out of capacity when
		 * assigning index to new entries
		 * @param idList the indices of possible palette entries and their full integer IDs;
		 * useful for palette serialization
		 * @param bits the number of bits each entry uses in the storage
		 */
		<A> Palette<A> create(int bits, IndexedIterable<A> idList, PaletteResizeListener<A> listener, List<A> list);
	}
}

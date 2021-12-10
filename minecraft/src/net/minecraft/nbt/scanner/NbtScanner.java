package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

/**
 * An NBT scanner may reduce unnecessary data parsing to allow finding
 * desired information in an NBT structure as fast as possible.
 * 
 * <p>Call format: start -> VisitBody
 * <br>
 * VisitBody:<br>
 * { visitEnd | visitString | visitByte | visitShort | visitInt<br>
 * | visitLong | visitFloat | visitDouble | visitByteArray<br>
 * | visitIntArray | visitLongArray<br>
 * | visitListMeta -> [startListItem -> VisitBody]* -> endNested<br>
 * | [visitSubNbtType -> startSubNbt -> VisitBody]* -> endNested<br>
 * }
 * 
 * <p>The visit order is depth-first.
 */
public interface NbtScanner {
	NbtScanner.Result visitEnd();

	NbtScanner.Result visitString(String value);

	NbtScanner.Result visitByte(byte value);

	NbtScanner.Result visitShort(short value);

	NbtScanner.Result visitInt(int value);

	NbtScanner.Result visitLong(long value);

	NbtScanner.Result visitFloat(float value);

	NbtScanner.Result visitDouble(double value);

	NbtScanner.Result visitByteArray(byte[] value);

	NbtScanner.Result visitIntArray(int[] value);

	NbtScanner.Result visitLongArray(long[] value);

	NbtScanner.Result visitListMeta(NbtType<?> entryType, int length);

	NbtScanner.NestedResult visitSubNbtType(NbtType<?> type);

	/**
	 * Starts a visit to an NBT structure in the sub NBT of a compound NBT.
	 * 
	 * @see #start
	 * @see #startListItem
	 */
	NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key);

	/**
	 * Starts a visit to an NBT structure in an element of a list NBT.
	 * 
	 * @see #startSubNbt
	 * @see #start
	 */
	NbtScanner.NestedResult startListItem(NbtType<?> type, int index);

	/**
	 * Ends a nested visit.
	 * 
	 * <p>This is guaranteed to be called once for each call to {@link #start},
	 * {@link #visitSubNbtType}, and {@link #startListItem} where the list or
	 * the compound NBT type is passed, unless the visit is halted.
	 */
	NbtScanner.Result endNested();

	/**
	 * Starts a visit to an NBT structure.
	 * 
	 * @see #startSubNbt
	 * @see #startListItem
	 */
	NbtScanner.Result start(NbtType<?> rootType);

	public static enum NestedResult {
		/**
		 * Proceeds to visit more data of this element, or to enter this element.
		 * (this element is a list element or a sub NBT)
		 */
		ENTER,
		/**
		 * Skips this element and visit the next list element or sub NBT.
		 */
		SKIP,
		/**
		 * Skips the whole list or compound NBT currently under scan. Will make a
		 * call to {@link NbtScanner#endNested()}.
		 */
		BREAK,
		/**
		 * Halts the whole scanning completely.
		 */
		HALT;
	}

	public static enum Result {
		/**
		 * Proceed to visit more data of this element.
		 */
		CONTINUE,
		/**
		 * Skips the current element under scan.
		 */
		BREAK,
		/**
		 * Halts the whole scanning completely.
		 */
		HALT;
	}
}

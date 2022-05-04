package net.minecraft.nbt;

/**
 * A class holding known NBT types.
 */
public class NbtTypes {
	private static final NbtType<?>[] VALUES = new NbtType[]{
		NbtEnd.TYPE,
		NbtByte.TYPE,
		NbtShort.TYPE,
		NbtInt.TYPE,
		NbtLong.TYPE,
		NbtFloat.TYPE,
		NbtDouble.TYPE,
		NbtByteArray.TYPE,
		NbtString.TYPE,
		NbtList.TYPE,
		NbtCompound.TYPE,
		NbtIntArray.TYPE,
		NbtLongArray.TYPE
	};

	/**
	 * Gets the associated {@linkplain NbtType NBT type} for a given {@code id}.
	 * <p>
	 * This method does not support id aliases.
	 * 
	 * @return the NBT type, or {@linkplain NbtType#createInvalid an invalid type} if there is no type with the given {@code id}
	 */
	public static NbtType<?> byId(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : NbtType.createInvalid(id);
	}
}

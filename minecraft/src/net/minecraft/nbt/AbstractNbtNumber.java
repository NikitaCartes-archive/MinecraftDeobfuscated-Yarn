package net.minecraft.nbt;

/**
 * Represents an NBT number.
 * <p>
 * The type {@link NbtElement#NUMBER_TYPE NUMBER_TYPE} can be used to
 * {@linkplain NbtCompound#contains check for the existence of any numeric element in a NBT compound object}.
 */
public abstract class AbstractNbtNumber implements NbtElement {
	protected AbstractNbtNumber() {
	}

	/**
	 * Gets the value as a 64-bit integer.
	 * 
	 * @return the value as a long
	 */
	public abstract long longValue();

	/**
	 * Gets the value as a 32-bit integer.
	 * 
	 * @return the value as an int
	 */
	public abstract int intValue();

	/**
	 * Gets the value as a 16-bit integer.
	 * 
	 * @return the value as a short
	 */
	public abstract short shortValue();

	/**
	 * Gets the value as an 8-bit integer.
	 * 
	 * @return the value as a byte
	 */
	public abstract byte byteValue();

	/**
	 * Gets the value as a 64-bit floating-point number.
	 * 
	 * @return the value as a double
	 */
	public abstract double doubleValue();

	/**
	 * Gets the value as a 32-bit floating-point number.
	 * 
	 * @return the value as a float
	 */
	public abstract float floatValue();

	/**
	 * Gets the value as a generic number.
	 * 
	 * @return the value as a {@link Number}
	 */
	public abstract Number numberValue();

	@Override
	public String toString() {
		return this.asString();
	}
}

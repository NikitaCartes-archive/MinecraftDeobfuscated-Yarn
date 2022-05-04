package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;

/**
 * Represents an NBT type.
 */
public interface NbtType<T extends NbtElement> {
	T read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException;

	NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException;

	default void accept(DataInput input, NbtScanner visitor) throws IOException {
		switch (visitor.start(this)) {
			case CONTINUE:
				this.doAccept(input, visitor);
			case HALT:
			default:
				break;
			case BREAK:
				this.skip(input);
		}
	}

	void skip(DataInput input, int count) throws IOException;

	void skip(DataInput input) throws IOException;

	/**
	 * Determines the immutability of this type.
	 * <p>
	 * The mutability of an NBT type means the held value can be modified
	 * after the NBT element is instantiated.
	 * 
	 * @return {@code true} if this NBT type is immutable, else {@code false}
	 */
	default boolean isImmutable() {
		return false;
	}

	String getCrashReportName();

	String getCommandFeedbackName();

	/**
	 * {@return an invalid NBT type}
	 * 
	 * <p>Operations with an invalid NBT type always throws {@link IOException}.
	 * 
	 * @see NbtTypes#byId(int)
	 */
	static NbtType<NbtEnd> createInvalid(int type) {
		return new NbtType<NbtEnd>() {
			private IOException createException() {
				return new IOException("Invalid tag id: " + type);
			}

			public NbtEnd read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
				throw this.createException();
			}

			@Override
			public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
				throw this.createException();
			}

			@Override
			public void skip(DataInput input, int count) throws IOException {
				throw this.createException();
			}

			@Override
			public void skip(DataInput input) throws IOException {
				throw this.createException();
			}

			@Override
			public String getCrashReportName() {
				return "INVALID[" + type + "]";
			}

			@Override
			public String getCommandFeedbackName() {
				return "UNKNOWN_" + type;
			}
		};
	}

	/**
	 * Represents an NBT type whose elements have a fixed size, such as primitives.
	 */
	public interface OfFixedSize<T extends NbtElement> extends NbtType<T> {
		@Override
		default void skip(DataInput input) throws IOException {
			input.skipBytes(this.getSizeInBytes());
		}

		@Override
		default void skip(DataInput input, int count) throws IOException {
			input.skipBytes(this.getSizeInBytes() * count);
		}

		/**
		 * {@return the size of the elements in bytes}
		 */
		int getSizeInBytes();
	}

	/**
	 * Represents an NBT type whose elements can have a variable size, such as lists.
	 */
	public interface OfVariableSize<T extends NbtElement> extends NbtType<T> {
		@Override
		default void skip(DataInput input, int count) throws IOException {
			for (int i = 0; i < count; i++) {
				this.skip(input);
			}
		}
	}
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.class_6836;

/**
 * Represents an NBT type.
 */
public interface NbtType<T extends NbtElement> {
	T read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException;

	class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException;

	default void method_39877(DataInput dataInput, class_6836 arg) throws IOException {
		switch (arg.method_39871(this)) {
			case CONTINUE:
				this.method_39852(dataInput, arg);
			case HALT:
			default:
				break;
			case BREAK:
				this.method_39851(dataInput);
		}
	}

	void method_39854(DataInput dataInput, int i) throws IOException;

	void method_39851(DataInput dataInput) throws IOException;

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

	static NbtType<NbtNull> createInvalid(int type) {
		return new NbtType<NbtNull>() {
			private IOException method_39878() {
				return new IOException("Invalid tag id: " + type);
			}

			public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
				throw this.method_39878();
			}

			@Override
			public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
				throw this.method_39878();
			}

			@Override
			public void method_39854(DataInput dataInput, int i) throws IOException {
				throw this.method_39878();
			}

			@Override
			public void method_39851(DataInput dataInput) throws IOException {
				throw this.method_39878();
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

	public interface class_6839<T extends NbtElement> extends NbtType<T> {
		@Override
		default void method_39851(DataInput dataInput) throws IOException {
			dataInput.skipBytes(this.method_39853());
		}

		@Override
		default void method_39854(DataInput dataInput, int i) throws IOException {
			dataInput.skipBytes(this.method_39853() * i);
		}

		int method_39853();
	}

	public interface class_6840<T extends NbtElement> extends NbtType<T> {
		@Override
		default void method_39854(DataInput dataInput, int i) throws IOException {
			for (int j = 0; j < i; j++) {
				this.method_39851(dataInput);
			}
		}
	}
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT byte. Its type is {@value NbtElement#BYTE_TYPE}.
 * Instances are immutable.
 */
public class NbtByte extends AbstractNbtNumber {
	private static final int SIZE = 9;
	public static final NbtType<NbtByte> TYPE = new NbtType.OfFixedSize<NbtByte>() {
		public NbtByte read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
			return NbtByte.of(readByte(dataInput, nbtSizeTracker));
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
			return visitor.visitByte(readByte(input, tracker));
		}

		private static byte readByte(DataInput input, NbtSizeTracker tracker) throws IOException {
			tracker.add(9L);
			return input.readByte();
		}

		@Override
		public int getSizeInBytes() {
			return 1;
		}

		@Override
		public String getCrashReportName() {
			return "BYTE";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Byte";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	/**
	 * The NBT byte representing {@code 0}.
	 * 
	 * @apiNote This is often used to indicate a false boolean value.
	 */
	public static final NbtByte ZERO = of((byte)0);
	/**
	 * The NBT byte representing {@code 1}.
	 * 
	 * @apiNote This is often used to indicate a true boolean value.
	 */
	public static final NbtByte ONE = of((byte)1);
	private final byte value;

	NbtByte(byte value) {
		this.value = value;
	}

	/**
	 * {@return the NBT byte from {@code value}}
	 * 
	 * @implNote This returns the value from the cache.
	 */
	public static NbtByte of(byte value) {
		return NbtByte.Cache.VALUES[128 + value];
	}

	/**
	 * {@return the NBT byte representing the boolean {@code value}}
	 */
	public static NbtByte of(boolean value) {
		return value ? ONE : ZERO;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(this.value);
	}

	@Override
	public int getSizeInBytes() {
		return 9;
	}

	@Override
	public byte getType() {
		return NbtElement.BYTE_TYPE;
	}

	@Override
	public NbtType<NbtByte> getNbtType() {
		return TYPE;
	}

	public NbtByte copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtByte && this.value == ((NbtByte)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitByte(this);
	}

	@Override
	public long longValue() {
		return (long)this.value;
	}

	@Override
	public int intValue() {
		return this.value;
	}

	@Override
	public short shortValue() {
		return (short)this.value;
	}

	@Override
	public byte byteValue() {
		return this.value;
	}

	@Override
	public double doubleValue() {
		return (double)this.value;
	}

	@Override
	public float floatValue() {
		return (float)this.value;
	}

	@Override
	public Number numberValue() {
		return this.value;
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitByte(this.value);
	}

	static class Cache {
		static final NbtByte[] VALUES = new NbtByte[256];

		private Cache() {
		}

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtByte((byte)(i - 128));
			}
		}
	}
}

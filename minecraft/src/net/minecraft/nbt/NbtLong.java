package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 64-bit integer. Its type is {@value NbtElement#LONG_TYPE}.
 * Instances are immutable.
 */
public class NbtLong extends AbstractNbtNumber {
	private static final int SIZE = 16;
	public static final NbtType<NbtLong> TYPE = new NbtType.OfFixedSize<NbtLong>() {
		public NbtLong read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
			return NbtLong.of(readLong(dataInput, nbtSizeTracker));
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
			return visitor.visitLong(readLong(input, tracker));
		}

		private static long readLong(DataInput input, NbtSizeTracker tracker) throws IOException {
			tracker.add(16L);
			return input.readLong();
		}

		@Override
		public int getSizeInBytes() {
			return 8;
		}

		@Override
		public String getCrashReportName() {
			return "LONG";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Long";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final long value;

	NbtLong(long value) {
		this.value = value;
	}

	/**
	 * {@return the NBT long from {@code value}}
	 */
	public static NbtLong of(long value) {
		return value >= -128L && value <= 1024L ? NbtLong.Cache.VALUES[(int)value - -128] : new NbtLong(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public int getSizeInBytes() {
		return 16;
	}

	@Override
	public byte getType() {
		return NbtElement.LONG_TYPE;
	}

	@Override
	public NbtType<NbtLong> getNbtType() {
		return TYPE;
	}

	public NbtLong copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtLong && this.value == ((NbtLong)o).value;
	}

	public int hashCode() {
		return (int)(this.value ^ this.value >>> 32);
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitLong(this);
	}

	@Override
	public long longValue() {
		return this.value;
	}

	@Override
	public int intValue() {
		return (int)(this.value & -1L);
	}

	@Override
	public short shortValue() {
		return (short)((int)(this.value & 65535L));
	}

	@Override
	public byte byteValue() {
		return (byte)((int)(this.value & 255L));
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
		return visitor.visitLong(this.value);
	}

	static class Cache {
		private static final int MAX = 1024;
		private static final int MIN = -128;
		static final NbtLong[] VALUES = new NbtLong[1153];

		private Cache() {
		}

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtLong((long)(-128 + i));
			}
		}
	}
}

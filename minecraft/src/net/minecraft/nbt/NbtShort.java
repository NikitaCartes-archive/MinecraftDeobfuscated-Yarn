package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 16-bit integer. Its type is {@value NbtElement#SHORT_TYPE}.
 * Instances are immutable.
 */
public class NbtShort extends AbstractNbtNumber {
	private static final int SIZE = 10;
	public static final NbtType<NbtShort> TYPE = new NbtType.OfFixedSize<NbtShort>() {
		public NbtShort read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
			return NbtShort.of(readShort(dataInput, nbtSizeTracker));
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
			return visitor.visitShort(readShort(input, tracker));
		}

		private static short readShort(DataInput input, NbtSizeTracker tracker) throws IOException {
			tracker.add(10L);
			return input.readShort();
		}

		@Override
		public int getSizeInBytes() {
			return 2;
		}

		@Override
		public String getCrashReportName() {
			return "SHORT";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Short";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final short value;

	NbtShort(short value) {
		this.value = value;
	}

	/**
	 * {@return the NBT short from {@code value}}
	 */
	public static NbtShort of(short value) {
		return value >= -128 && value <= 1024 ? NbtShort.Cache.VALUES[value - -128] : new NbtShort(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
	}

	@Override
	public int getSizeInBytes() {
		return 10;
	}

	@Override
	public byte getType() {
		return NbtElement.SHORT_TYPE;
	}

	@Override
	public NbtType<NbtShort> getNbtType() {
		return TYPE;
	}

	public NbtShort copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtShort && this.value == ((NbtShort)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitShort(this);
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
		return this.value;
	}

	@Override
	public byte byteValue() {
		return (byte)(this.value & 255);
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
		return visitor.visitShort(this.value);
	}

	static class Cache {
		private static final int MAX = 1024;
		private static final int MIN = -128;
		static final NbtShort[] VALUES = new NbtShort[1153];

		private Cache() {
		}

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtShort((short)(-128 + i));
			}
		}
	}
}

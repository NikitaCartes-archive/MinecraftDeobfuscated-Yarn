package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 32-bit integer. Its type is {@value NbtElement#INT_TYPE}.
 * Instances are immutable.
 */
public class NbtInt extends AbstractNbtNumber {
	private static final int SIZE = 12;
	public static final NbtType<NbtInt> TYPE = new NbtType.OfFixedSize<NbtInt>() {
		public NbtInt read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
			return NbtInt.of(readInt(dataInput, nbtSizeTracker));
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
			return visitor.visitInt(readInt(input, tracker));
		}

		private static int readInt(DataInput input, NbtSizeTracker tracker) throws IOException {
			tracker.add(12L);
			return input.readInt();
		}

		@Override
		public int getSizeInBytes() {
			return 4;
		}

		@Override
		public String getCrashReportName() {
			return "INT";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Int";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final int value;

	NbtInt(int value) {
		this.value = value;
	}

	/**
	 * {@return the NBT integer from {@code value}}
	 */
	public static NbtInt of(int value) {
		return value >= -128 && value <= 1024 ? NbtInt.Cache.VALUES[value - -128] : new NbtInt(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value);
	}

	@Override
	public int getSizeInBytes() {
		return 12;
	}

	@Override
	public byte getType() {
		return NbtElement.INT_TYPE;
	}

	@Override
	public NbtType<NbtInt> getNbtType() {
		return TYPE;
	}

	public NbtInt copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtInt && this.value == ((NbtInt)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitInt(this);
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
		return (short)(this.value & 65535);
	}

	@Override
	public byte byteValue() {
		return (byte)(this.value & 0xFF);
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
		return visitor.visitInt(this.value);
	}

	static class Cache {
		private static final int MAX = 1024;
		private static final int MIN = -128;
		static final NbtInt[] VALUES = new NbtInt[1153];

		private Cache() {
		}

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtInt(-128 + i);
			}
		}
	}
}

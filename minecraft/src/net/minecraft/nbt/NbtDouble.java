package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.math.MathHelper;

/**
 * Represents an NBT 64-bit floating-point number. Its type is {@value NbtElement#DOUBLE_TYPE}.
 * Instances are immutable.
 */
public class NbtDouble extends AbstractNbtNumber {
	private static final int SIZE = 16;
	/**
	 * The NBT double representing {@code 0.0}.
	 */
	public static final NbtDouble ZERO = new NbtDouble(0.0);
	public static final NbtType<NbtDouble> TYPE = new NbtType.OfFixedSize<NbtDouble>() {
		public NbtDouble read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(16L);
			return NbtDouble.of(dataInput.readDouble());
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
			return visitor.visitDouble(input.readDouble());
		}

		@Override
		public int getSizeInBytes() {
			return 8;
		}

		@Override
		public String getCrashReportName() {
			return "DOUBLE";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Double";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final double value;

	private NbtDouble(double value) {
		this.value = value;
	}

	/**
	 * {@return the NBT double from {@code value}}
	 */
	public static NbtDouble of(double value) {
		return value == 0.0 ? ZERO : new NbtDouble(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeDouble(this.value);
	}

	@Override
	public int getSizeInBytes() {
		return 16;
	}

	@Override
	public byte getType() {
		return NbtElement.DOUBLE_TYPE;
	}

	@Override
	public NbtType<NbtDouble> getNbtType() {
		return TYPE;
	}

	public NbtDouble copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtDouble && this.value == ((NbtDouble)o).value;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.value);
		return (int)(l ^ l >>> 32);
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitDouble(this);
	}

	@Override
	public long longValue() {
		return (long)Math.floor(this.value);
	}

	@Override
	public int intValue() {
		return MathHelper.floor(this.value);
	}

	@Override
	public short shortValue() {
		return (short)(MathHelper.floor(this.value) & 65535);
	}

	@Override
	public byte byteValue() {
		return (byte)(MathHelper.floor(this.value) & 0xFF);
	}

	@Override
	public double doubleValue() {
		return this.value;
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
		return visitor.visitDouble(this.value);
	}
}

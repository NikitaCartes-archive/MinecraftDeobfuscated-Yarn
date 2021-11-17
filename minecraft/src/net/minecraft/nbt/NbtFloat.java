package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_6836;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.math.MathHelper;

/**
 * Represents an NBT 32-bit floating-point number.
 */
public class NbtFloat extends AbstractNbtNumber {
	private static final int SIZE = 96;
	public static final NbtFloat ZERO = new NbtFloat(0.0F);
	public static final NbtType<NbtFloat> TYPE = new NbtType.class_6839<NbtFloat>() {
		public NbtFloat read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(96L);
			return NbtFloat.of(dataInput.readFloat());
		}

		@Override
		public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
			return arg.method_39859(dataInput.readFloat());
		}

		@Override
		public int method_39853() {
			return 4;
		}

		@Override
		public String getCrashReportName() {
			return "FLOAT";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Float";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private final float value;

	private NbtFloat(float value) {
		this.value = value;
	}

	public static NbtFloat of(float value) {
		return value == 0.0F ? ZERO : new NbtFloat(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeFloat(this.value);
	}

	@Override
	public byte getType() {
		return NbtElement.FLOAT_TYPE;
	}

	@Override
	public NbtType<NbtFloat> getNbtType() {
		return TYPE;
	}

	public NbtFloat copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtFloat && this.value == ((NbtFloat)o).value;
	}

	public int hashCode() {
		return Float.floatToIntBits(this.value);
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitFloat(this);
	}

	@Override
	public long longValue() {
		return (long)this.value;
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
		return (double)this.value;
	}

	@Override
	public float floatValue() {
		return this.value;
	}

	@Override
	public Number numberValue() {
		return this.value;
	}

	@Override
	public class_6836.class_6838 method_39850(class_6836 arg) {
		return arg.method_39859(this.value);
	}
}

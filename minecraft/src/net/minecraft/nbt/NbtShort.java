package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_6836;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 16-bit integer.
 */
public class NbtShort extends AbstractNbtNumber {
	private static final int SIZE = 80;
	public static final NbtType<NbtShort> TYPE = new NbtType.class_6839<NbtShort>() {
		public NbtShort read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(80L);
			return NbtShort.of(dataInput.readShort());
		}

		@Override
		public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
			return arg.method_39866(dataInput.readShort());
		}

		@Override
		public int method_39853() {
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

	public static NbtShort of(short value) {
		return value >= -128 && value <= 1024 ? NbtShort.Cache.VALUES[value - -128] : new NbtShort(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
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
	public class_6836.class_6838 method_39850(class_6836 arg) {
		return arg.method_39866(this.value);
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

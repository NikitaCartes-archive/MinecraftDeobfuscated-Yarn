package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_6836;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT 32-bit integer.
 */
public class NbtInt extends AbstractNbtNumber {
	private static final int SIZE = 96;
	public static final NbtType<NbtInt> TYPE = new NbtType.class_6839<NbtInt>() {
		public NbtInt read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(96L);
			return NbtInt.of(dataInput.readInt());
		}

		@Override
		public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
			return arg.method_39860(dataInput.readInt());
		}

		@Override
		public int method_39853() {
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

	public static NbtInt of(int value) {
		return value >= -128 && value <= 1024 ? NbtInt.Cache.VALUES[value - -128] : new NbtInt(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value);
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
	public class_6836.class_6838 method_39850(class_6836 arg) {
		return arg.method_39860(this.value);
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

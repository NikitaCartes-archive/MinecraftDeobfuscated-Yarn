package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_6836;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT byte.
 */
public class NbtByte extends AbstractNbtNumber {
	private static final int SIZE = 72;
	public static final NbtType<NbtByte> TYPE = new NbtType.class_6839<NbtByte>() {
		public NbtByte read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(72L);
			return NbtByte.of(dataInput.readByte());
		}

		@Override
		public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
			return arg.method_39857(dataInput.readByte());
		}

		@Override
		public int method_39853() {
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
	public static final NbtByte ZERO = of((byte)0);
	public static final NbtByte ONE = of((byte)1);
	private final byte value;

	NbtByte(byte value) {
		this.value = value;
	}

	public static NbtByte of(byte value) {
		return NbtByte.Cache.VALUES[128 + value];
	}

	public static NbtByte of(boolean value) {
		return value ? ONE : ZERO;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(this.value);
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
	public class_6836.class_6838 method_39850(class_6836 arg) {
		return arg.method_39857(this.value);
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

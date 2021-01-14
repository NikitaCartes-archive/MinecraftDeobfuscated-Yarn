package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * Represents an NBT 32-bit integer.
 */
public class NbtInt extends AbstractNbtNumber {
	public static final NbtType<NbtInt> TYPE = new NbtType<NbtInt>() {
		public NbtInt read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(96L);
			return NbtInt.of(dataInput.readInt());
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

	private NbtInt(int value) {
		this.value = value;
	}

	public static NbtInt of(int value) {
		return value >= -128 && value <= 1024 ? NbtInt.Cache.VALUES[value + 128] : new NbtInt(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value);
	}

	@Override
	public byte getType() {
		return 3;
	}

	@Override
	public NbtType<NbtInt> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
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
	public Text toText(String indent, int depth) {
		return new LiteralText(String.valueOf(this.value)).formatted(GOLD);
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

	static class Cache {
		static final NbtInt[] VALUES = new NbtInt[1153];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtInt(-128 + i);
			}
		}
	}
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * Represents an NBT 64-bit integer.
 */
public class NbtLong extends AbstractNbtNumber {
	public static final NbtType<NbtLong> TYPE = new NbtType<NbtLong>() {
		public NbtLong read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(128L);
			return NbtLong.of(dataInput.readLong());
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

	private NbtLong(long value) {
		this.value = value;
	}

	public static NbtLong of(long value) {
		return value >= -128L && value <= 1024L ? NbtLong.Cache.VALUES[(int)value + 128] : new NbtLong(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public NbtType<NbtLong> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.value + "L";
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
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("L").formatted(RED);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
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

	static class Cache {
		static final NbtLong[] VALUES = new NbtLong[1153];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new NbtLong((long)(-128 + i));
			}
		}
	}
}

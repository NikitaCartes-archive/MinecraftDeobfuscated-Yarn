package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class LongTag extends AbstractNumberTag {
	public static final TagReader<LongTag> READER = new TagReader<LongTag>() {
		public LongTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(128L);
			return LongTag.of(dataInput.readLong());
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

	private LongTag(long value) {
		this.value = value;
	}

	public static LongTag of(long value) {
		return value >= -128L && value <= 1024L ? LongTag.Cache.VALUES[(int)value + 128] : new LongTag(value);
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
	public TagReader<LongTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.value + "L";
	}

	public LongTag copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof LongTag && this.value == ((LongTag)o).value;
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
	public long getLong() {
		return this.value;
	}

	@Override
	public int getInt() {
		return (int)(this.value & -1L);
	}

	@Override
	public short getShort() {
		return (short)((int)(this.value & 65535L));
	}

	@Override
	public byte getByte() {
		return (byte)((int)(this.value & 255L));
	}

	@Override
	public double getDouble() {
		return (double)this.value;
	}

	@Override
	public float getFloat() {
		return (float)this.value;
	}

	@Override
	public Number getNumber() {
		return this.value;
	}

	static class Cache {
		static final LongTag[] VALUES = new LongTag[1153];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new LongTag((long)(-128 + i));
			}
		}
	}
}

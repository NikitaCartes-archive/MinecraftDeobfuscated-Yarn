package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ShortTag extends AbstractNumberTag {
	public static final TagReader<ShortTag> READER = new TagReader<ShortTag>() {
		public ShortTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(80L);
			return ShortTag.of(dataInput.readShort());
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

	private ShortTag(short value) {
		this.value = value;
	}

	public static ShortTag of(short value) {
		return value >= -128 && value <= 1024 ? ShortTag.Cache.VALUES[value + 128] : new ShortTag(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
	}

	@Override
	public byte getType() {
		return 2;
	}

	@Override
	public TagReader<ShortTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.value + "s";
	}

	public ShortTag copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof ShortTag && this.value == ((ShortTag)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("s").formatted(RED);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
	}

	@Override
	public long getLong() {
		return (long)this.value;
	}

	@Override
	public int getInt() {
		return this.value;
	}

	@Override
	public short getShort() {
		return this.value;
	}

	@Override
	public byte getByte() {
		return (byte)(this.value & 255);
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
		static final ShortTag[] VALUES = new ShortTag[1153];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new ShortTag((short)(-128 + i));
			}
		}
	}
}

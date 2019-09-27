package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ByteTag extends AbstractNumberTag {
	public static final TagReader<ByteTag> READER = new TagReader<ByteTag>() {
		public ByteTag method_23235(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(72L);
			return ByteTag.of(dataInput.readByte());
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
	public static final ByteTag ZERO = of((byte)0);
	public static final ByteTag ONE = of((byte)1);
	private final byte value;

	private ByteTag(byte b) {
		this.value = b;
	}

	public static ByteTag of(byte b) {
		return ByteTag.Cache.VALUES[128 + b];
	}

	public static ByteTag of(boolean bl) {
		return bl ? ONE : ZERO;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(this.value);
	}

	@Override
	public byte getType() {
		return 1;
	}

	@Override
	public TagReader<ByteTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.value + "b";
	}

	public ByteTag method_10530() {
		return this;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof ByteTag && this.value == ((ByteTag)object).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
		Text text = new LiteralText("b").formatted(RED);
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
		return (short)this.value;
	}

	@Override
	public byte getByte() {
		return this.value;
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
		private static final ByteTag[] VALUES = new ByteTag[256];

		static {
			for (int i = 0; i < VALUES.length; i++) {
				VALUES[i] = new ByteTag((byte)(i - 128));
			}
		}
	}
}

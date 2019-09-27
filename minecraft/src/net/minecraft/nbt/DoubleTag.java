package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DoubleTag extends AbstractNumberTag {
	public static final DoubleTag ZERO = new DoubleTag(0.0);
	public static final TagReader<DoubleTag> READER = new TagReader<DoubleTag>() {
		public DoubleTag method_23242(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(128L);
			return DoubleTag.of(dataInput.readDouble());
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

	private DoubleTag(double d) {
		this.value = d;
	}

	public static DoubleTag of(double d) {
		return d == 0.0 ? ZERO : new DoubleTag(d);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(this.value);
	}

	@Override
	public byte getType() {
		return 6;
	}

	@Override
	public TagReader<DoubleTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.value + "d";
	}

	public DoubleTag method_10585() {
		return this;
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof DoubleTag && this.value == ((DoubleTag)object).value;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.value);
		return (int)(l ^ l >>> 32);
	}

	@Override
	public Text toText(String string, int i) {
		Text text = new LiteralText("d").formatted(RED);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
	}

	@Override
	public long getLong() {
		return (long)Math.floor(this.value);
	}

	@Override
	public int getInt() {
		return MathHelper.floor(this.value);
	}

	@Override
	public short getShort() {
		return (short)(MathHelper.floor(this.value) & 65535);
	}

	@Override
	public byte getByte() {
		return (byte)(MathHelper.floor(this.value) & 0xFF);
	}

	@Override
	public double getDouble() {
		return this.value;
	}

	@Override
	public float getFloat() {
		return (float)this.value;
	}

	@Override
	public Number getNumber() {
		return this.value;
	}
}

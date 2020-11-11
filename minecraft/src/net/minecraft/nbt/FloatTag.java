package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtTagVisitor;
import net.minecraft.util.math.MathHelper;

public class FloatTag extends AbstractNumberTag {
	public static final FloatTag ZERO = new FloatTag(0.0F);
	public static final TagReader<FloatTag> READER = new TagReader<FloatTag>() {
		public FloatTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(96L);
			return FloatTag.of(dataInput.readFloat());
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

	private FloatTag(float value) {
		this.value = value;
	}

	public static FloatTag of(float value) {
		return value == 0.0F ? ZERO : new FloatTag(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeFloat(this.value);
	}

	@Override
	public byte getType() {
		return 5;
	}

	@Override
	public TagReader<FloatTag> getReader() {
		return READER;
	}

	public FloatTag copy() {
		return this;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof FloatTag && this.value == ((FloatTag)o).value;
	}

	public int hashCode() {
		return Float.floatToIntBits(this.value);
	}

	@Override
	public void accept(NbtTagVisitor visitor) {
		visitor.visitFloatTag(this);
	}

	@Override
	public long getLong() {
		return (long)this.value;
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
		return (double)this.value;
	}

	@Override
	public float getFloat() {
		return this.value;
	}

	@Override
	public Number getNumber() {
		return this.value;
	}
}

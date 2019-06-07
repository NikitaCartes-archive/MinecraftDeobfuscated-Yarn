package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DoubleTag extends AbstractNumberTag {
	private double value;

	DoubleTag() {
	}

	public DoubleTag(double d) {
		this.value = d;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(128L);
		this.value = dataInput.readDouble();
	}

	@Override
	public byte getType() {
		return 6;
	}

	@Override
	public String toString() {
		return this.value + "d";
	}

	public DoubleTag method_10585() {
		return new DoubleTag(this.value);
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

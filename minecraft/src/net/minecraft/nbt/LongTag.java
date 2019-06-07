package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class LongTag extends AbstractNumberTag {
	private long value;

	LongTag() {
	}

	public LongTag(long l) {
		this.value = l;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeLong(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(128L);
		this.value = dataInput.readLong();
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public String toString() {
		return this.value + "L";
	}

	public LongTag method_10621() {
		return new LongTag(this.value);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof LongTag && this.value == ((LongTag)object).value;
	}

	public int hashCode() {
		return (int)(this.value ^ this.value >>> 32);
	}

	@Override
	public Text toText(String string, int i) {
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
}

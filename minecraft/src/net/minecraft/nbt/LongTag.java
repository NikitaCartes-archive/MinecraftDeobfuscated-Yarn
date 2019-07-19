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
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(128L);
		this.value = input.readLong();
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public String toString() {
		return this.value + "L";
	}

	public LongTag copy() {
		return new LongTag(this.value);
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
}

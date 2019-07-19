package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ShortTag extends AbstractNumberTag {
	private short value;

	public ShortTag() {
	}

	public ShortTag(short s) {
		this.value = s;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(80L);
		this.value = input.readShort();
	}

	@Override
	public byte getType() {
		return 2;
	}

	@Override
	public String toString() {
		return this.value + "s";
	}

	public ShortTag copy() {
		return new ShortTag(this.value);
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
}

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
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeShort(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(80L);
		this.value = dataInput.readShort();
	}

	@Override
	public byte getType() {
		return 2;
	}

	@Override
	public String toString() {
		return this.value + "s";
	}

	public ShortTag method_10704() {
		return new ShortTag(this.value);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof ShortTag && this.value == ((ShortTag)object).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
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

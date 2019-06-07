package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ByteTag extends AbstractNumberTag {
	private byte value;

	ByteTag() {
	}

	public ByteTag(byte b) {
		this.value = b;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(72L);
		this.value = dataInput.readByte();
	}

	@Override
	public byte getType() {
		return 1;
	}

	@Override
	public String toString() {
		return this.value + "b";
	}

	public ByteTag method_10530() {
		return new ByteTag(this.value);
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
}

package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class IntTag extends AbstractNumberTag {
	private int value;

	IntTag() {
	}

	public IntTag(int i) {
		this.value = i;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(96L);
		this.value = dataInput.readInt();
	}

	@Override
	public byte getType() {
		return 3;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	public IntTag method_10592() {
		return new IntTag(this.value);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof IntTag && this.value == ((IntTag)object).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
		return new LiteralText(String.valueOf(this.value)).formatted(GOLD);
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
		return (short)(this.value & 65535);
	}

	@Override
	public byte getByte() {
		return (byte)(this.value & 0xFF);
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

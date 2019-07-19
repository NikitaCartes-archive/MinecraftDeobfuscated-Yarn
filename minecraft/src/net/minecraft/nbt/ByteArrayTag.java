package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag extends AbstractListTag<ByteTag> {
	private byte[] value;

	ByteArrayTag() {
	}

	public ByteArrayTag(byte[] bs) {
		this.value = bs;
	}

	public ByteArrayTag(List<Byte> list) {
		this(toArray(list));
	}

	private static byte[] toArray(List<Byte> list) {
		byte[] bs = new byte[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Byte byte_ = (Byte)list.get(i);
			bs[i] = byte_ == null ? 0 : byte_;
		}

		return bs;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);
		output.write(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(192L);
		int i = input.readInt();
		positionTracker.add((long)(8 * i));
		this.value = new byte[i];
		input.readFully(this.value);
	}

	@Override
	public byte getType() {
		return 7;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[B;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('B');
		}

		return stringBuilder.append(']').toString();
	}

	@Override
	public Tag copy() {
		byte[] bs = new byte[this.value.length];
		System.arraycopy(this.value, 0, bs, 0, this.value.length);
		return new ByteArrayTag(bs);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("B").formatted(RED);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			Text text3 = new LiteralText(String.valueOf(this.value[i])).formatted(GOLD);
			text2.append(" ").append(text3).append(text);
			if (i != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	public byte[] getByteArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public ByteTag get(int i) {
		return new ByteTag(this.value[i]);
	}

	public ByteTag method_10606(int i, ByteTag byteTag) {
		byte b = this.value[i];
		this.value[i] = byteTag.getByte();
		return new ByteTag(b);
	}

	public void add(int i, ByteTag byteTag) {
		this.value = ArrayUtils.add(this.value, i, byteTag.getByte());
	}

	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[index] = ((AbstractNumberTag)tag).getByte();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getByte());
			return true;
		} else {
			return false;
		}
	}

	public ByteTag remove(int i) {
		byte b = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return new ByteTag(b);
	}

	public void clear() {
		this.value = new byte[0];
	}
}

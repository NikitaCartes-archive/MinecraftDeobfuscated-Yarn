package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
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
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.value.length);
		dataOutput.write(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(192L);
		int j = dataInput.readInt();
		positionTracker.add((long)(8 * j));
		this.value = new byte[j];
		dataInput.readFully(this.value);
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

	public boolean equals(Object object) {
		return this == object ? true : object instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)object).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public TextComponent method_10710(String string, int i) {
		TextComponent textComponent = new StringTextComponent("B").applyFormat(RED);
		TextComponent textComponent2 = new StringTextComponent("[").append(textComponent).append(";");

		for (int j = 0; j < this.value.length; j++) {
			TextComponent textComponent3 = new StringTextComponent(String.valueOf(this.value[j])).applyFormat(GOLD);
			textComponent2.append(" ").append(textComponent3).append(textComponent);
			if (j != this.value.length - 1) {
				textComponent2.append(",");
			}
		}

		textComponent2.append("]");
		return textComponent2;
	}

	public byte[] getByteArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public ByteTag method_10523(int i) {
		return new ByteTag(this.value[i]);
	}

	public ByteTag method_17803(int i, ByteTag byteTag) {
		byte b = this.value[i];
		this.value[i] = byteTag.getByte();
		return new ByteTag(b);
	}

	public void method_17805(int i, ByteTag byteTag) {
		this.value = ArrayUtils.add(this.value, i, byteTag.getByte());
	}

	@Override
	public boolean method_10535(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[i] = ((AbstractNumberTag)tag).getByte();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_10533(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, i, ((AbstractNumberTag)tag).getByte());
			return true;
		} else {
			return false;
		}
	}

	public ByteTag method_17804(int i) {
		byte b = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return new ByteTag(b);
	}

	public void clear() {
		this.value = new byte[0];
	}
}

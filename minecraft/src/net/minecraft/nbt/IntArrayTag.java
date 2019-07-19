package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends AbstractListTag<IntTag> {
	private int[] value;

	IntArrayTag() {
	}

	public IntArrayTag(int[] is) {
		this.value = is;
	}

	public IntArrayTag(List<Integer> list) {
		this(toArray(list));
	}

	private static int[] toArray(List<Integer> list) {
		int[] is = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Integer integer = (Integer)list.get(i);
			is[i] = integer == null ? 0 : integer;
		}

		return is;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (int i : this.value) {
			output.writeInt(i);
		}
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(192L);
		int i = input.readInt();
		positionTracker.add((long)(32 * i));
		this.value = new int[i];

		for (int j = 0; j < i; j++) {
			this.value[j] = input.readInt();
		}
	}

	@Override
	public byte getType() {
		return 11;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[I;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]);
		}

		return stringBuilder.append(']').toString();
	}

	public IntArrayTag copy() {
		int[] is = new int[this.value.length];
		System.arraycopy(this.value, 0, is, 0, this.value.length);
		return new IntArrayTag(is);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof IntArrayTag && Arrays.equals(this.value, ((IntArrayTag)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	public int[] getIntArray() {
		return this.value;
	}

	@Override
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("I").formatted(RED);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			text2.append(" ").append(new LiteralText(String.valueOf(this.value[i])).formatted(GOLD));
			if (i != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	public int size() {
		return this.value.length;
	}

	public IntTag get(int i) {
		return new IntTag(this.value[i]);
	}

	public IntTag set(int i, IntTag intTag) {
		int j = this.value[i];
		this.value[i] = intTag.getInt();
		return new IntTag(j);
	}

	public void method_10531(int i, IntTag intTag) {
		this.value = ArrayUtils.add(this.value, i, intTag.getInt());
	}

	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[index] = ((AbstractNumberTag)tag).getInt();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getInt());
			return true;
		} else {
			return false;
		}
	}

	public IntTag method_10536(int i) {
		int j = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return new IntTag(j);
	}

	public void clear() {
		this.value = new int[0];
	}
}

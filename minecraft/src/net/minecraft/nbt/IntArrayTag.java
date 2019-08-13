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
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.value.length);

		for (int i : this.value) {
			dataOutput.writeInt(i);
		}
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(192L);
		int j = dataInput.readInt();
		positionTracker.add((long)(32 * j));
		this.value = new int[j];

		for (int k = 0; k < j; k++) {
			this.value[k] = dataInput.readInt();
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

	public IntArrayTag method_10591() {
		int[] is = new int[this.value.length];
		System.arraycopy(this.value, 0, is, 0, this.value.length);
		return new IntArrayTag(is);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof IntArrayTag && Arrays.equals(this.value, ((IntArrayTag)object).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	public int[] getIntArray() {
		return this.value;
	}

	@Override
	public Text toText(String string, int i) {
		Text text = new LiteralText("I").formatted(RED);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int j = 0; j < this.value.length; j++) {
			text2.append(" ").append(new LiteralText(String.valueOf(this.value[j])).formatted(GOLD));
			if (j != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	public int size() {
		return this.value.length;
	}

	public IntTag method_10589(int i) {
		return new IntTag(this.value[i]);
	}

	public IntTag method_17806(int i, IntTag intTag) {
		int j = this.value[i];
		this.value[i] = intTag.getInt();
		return new IntTag(j);
	}

	public void method_17808(int i, IntTag intTag) {
		this.value = ArrayUtils.add(this.value, i, intTag.getInt());
	}

	@Override
	public boolean setTag(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[i] = ((AbstractNumberTag)tag).getInt();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, i, ((AbstractNumberTag)tag).getInt());
			return true;
		} else {
			return false;
		}
	}

	public IntTag method_17807(int i) {
		int j = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return new IntTag(j);
	}

	public void clear() {
		this.value = new int[0];
	}
}

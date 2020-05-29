package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends AbstractListTag<IntTag> {
	public static final TagReader<IntArrayTag> READER = new TagReader<IntArrayTag>() {
		public IntArrayTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(192L);
			int j = dataInput.readInt();
			positionTracker.add(32L * (long)j);
			int[] is = new int[j];

			for (int k = 0; k < j; k++) {
				is[k] = dataInput.readInt();
			}

			return new IntArrayTag(is);
		}

		@Override
		public String getCrashReportName() {
			return "INT[]";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Int_Array";
		}
	};
	private int[] value;

	public IntArrayTag(int[] value) {
		this.value = value;
	}

	public IntArrayTag(List<Integer> value) {
		this(toArray(value));
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
	public byte getType() {
		return 11;
	}

	@Override
	public TagReader<IntArrayTag> getReader() {
		return READER;
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
		MutableText mutableText = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			mutableText.append(" ").append(new LiteralText(String.valueOf(this.value[i])).formatted(GOLD));
			if (i != this.value.length - 1) {
				mutableText.append(",");
			}
		}

		mutableText.append("]");
		return mutableText;
	}

	public int size() {
		return this.value.length;
	}

	public IntTag get(int i) {
		return IntTag.of(this.value[i]);
	}

	public IntTag set(int i, IntTag intTag) {
		int j = this.value[i];
		this.value[i] = intTag.getInt();
		return IntTag.of(j);
	}

	public void add(int i, IntTag intTag) {
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

	public IntTag remove(int i) {
		int j = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return IntTag.of(j);
	}

	@Override
	public byte getElementType() {
		return 3;
	}

	public void clear() {
		this.value = new int[0];
	}
}

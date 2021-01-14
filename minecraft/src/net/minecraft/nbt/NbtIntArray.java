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

/**
 * Represents an NBT 32-bit integer array.
 */
public class NbtIntArray extends AbstractNbtList<NbtInt> {
	public static final NbtType<NbtIntArray> TYPE = new NbtType<NbtIntArray>() {
		public NbtIntArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(192L);
			int j = dataInput.readInt();
			nbtTagSizeTracker.add(32L * (long)j);
			int[] is = new int[j];

			for (int k = 0; k < j; k++) {
				is[k] = dataInput.readInt();
			}

			return new NbtIntArray(is);
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

	public NbtIntArray(int[] value) {
		this.value = value;
	}

	public NbtIntArray(List<Integer> value) {
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
	public NbtType<NbtIntArray> getNbtType() {
		return TYPE;
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

	public NbtIntArray copy() {
		int[] is = new int[this.value.length];
		System.arraycopy(this.value, 0, is, 0, this.value.length);
		return new NbtIntArray(is);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtIntArray && Arrays.equals(this.value, ((NbtIntArray)o).value);
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

	public NbtInt get(int i) {
		return NbtInt.of(this.value[i]);
	}

	public NbtInt set(int i, NbtInt nbtInt) {
		int j = this.value[i];
		this.value[i] = nbtInt.intValue();
		return NbtInt.of(j);
	}

	public void add(int i, NbtInt nbtInt) {
		this.value = ArrayUtils.add(this.value, i, nbtInt.intValue());
	}

	@Override
	public boolean setElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value[index] = ((AbstractNbtNumber)element).intValue();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNbtNumber)element).intValue());
			return true;
		} else {
			return false;
		}
	}

	public NbtInt remove(int i) {
		int j = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return NbtInt.of(j);
	}

	@Override
	public byte getHeldType() {
		return 3;
	}

	public void clear() {
		this.value = new int[0];
	}
}

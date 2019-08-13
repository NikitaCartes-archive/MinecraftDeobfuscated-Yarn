package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends AbstractListTag<LongTag> {
	private long[] value;

	LongArrayTag() {
	}

	public LongArrayTag(long[] ls) {
		this.value = ls;
	}

	public LongArrayTag(LongSet longSet) {
		this.value = longSet.toLongArray();
	}

	public LongArrayTag(List<Long> list) {
		this(toArray(list));
	}

	private static long[] toArray(List<Long> list) {
		long[] ls = new long[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Long long_ = (Long)list.get(i);
			ls[i] = long_ == null ? 0L : long_;
		}

		return ls;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.value.length);

		for (long l : this.value) {
			dataOutput.writeLong(l);
		}
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(192L);
		int j = dataInput.readInt();
		positionTracker.add((long)(64 * j));
		this.value = new long[j];

		for (int k = 0; k < j; k++) {
			this.value[k] = dataInput.readLong();
		}
	}

	@Override
	public byte getType() {
		return 12;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[L;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('L');
		}

		return stringBuilder.append(']').toString();
	}

	public LongArrayTag method_10618() {
		long[] ls = new long[this.value.length];
		System.arraycopy(this.value, 0, ls, 0, this.value.length);
		return new LongArrayTag(ls);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)object).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text toText(String string, int i) {
		Text text = new LiteralText("L").formatted(RED);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int j = 0; j < this.value.length; j++) {
			Text text3 = new LiteralText(String.valueOf(this.value[j])).formatted(GOLD);
			text2.append(" ").append(text3).append(text);
			if (j != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	public long[] getLongArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public LongTag method_10616(int i) {
		return new LongTag(this.value[i]);
	}

	public LongTag method_17810(int i, LongTag longTag) {
		long l = this.value[i];
		this.value[i] = longTag.getLong();
		return new LongTag(l);
	}

	public void method_17812(int i, LongTag longTag) {
		this.value = ArrayUtils.add(this.value, i, longTag.getLong());
	}

	@Override
	public boolean setTag(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[i] = ((AbstractNumberTag)tag).getLong();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int i, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, i, ((AbstractNumberTag)tag).getLong());
			return true;
		} else {
			return false;
		}
	}

	public LongTag method_17811(int i) {
		long l = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return new LongTag(l);
	}

	public void clear() {
		this.value = new long[0];
	}
}

package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends AbstractListTag<LongTag> {
	public static final TagReader<LongArrayTag> READER = new TagReader<LongArrayTag>() {
		public LongArrayTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(192L);
			int j = dataInput.readInt();
			positionTracker.add(64L * (long)j);
			long[] ls = new long[j];

			for (int k = 0; k < j; k++) {
				ls[k] = dataInput.readLong();
			}

			return new LongArrayTag(ls);
		}

		@Override
		public String getCrashReportName() {
			return "LONG[]";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Long_Array";
		}
	};
	private long[] value;

	public LongArrayTag(long[] value) {
		this.value = value;
	}

	public LongArrayTag(LongSet value) {
		this.value = value.toLongArray();
	}

	public LongArrayTag(List<Long> value) {
		this(toArray(value));
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
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (long l : this.value) {
			output.writeLong(l);
		}
	}

	@Override
	public byte getType() {
		return 12;
	}

	@Override
	public TagReader<LongArrayTag> getReader() {
		return READER;
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

	public LongArrayTag copy() {
		long[] ls = new long[this.value.length];
		System.arraycopy(this.value, 0, ls, 0, this.value.length);
		return new LongArrayTag(ls);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("L").formatted(RED);
		MutableText mutableText = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			MutableText mutableText2 = new LiteralText(String.valueOf(this.value[i])).formatted(GOLD);
			mutableText.append(" ").append(mutableText2).append(text);
			if (i != this.value.length - 1) {
				mutableText.append(",");
			}
		}

		mutableText.append("]");
		return mutableText;
	}

	public long[] getLongArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public LongTag get(int i) {
		return LongTag.of(this.value[i]);
	}

	public LongTag method_10606(int i, LongTag longTag) {
		long l = this.value[i];
		this.value[i] = longTag.getLong();
		return LongTag.of(l);
	}

	public void add(int i, LongTag longTag) {
		this.value = ArrayUtils.add(this.value, i, longTag.getLong());
	}

	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[index] = ((AbstractNumberTag)tag).getLong();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getLong());
			return true;
		} else {
			return false;
		}
	}

	public LongTag remove(int i) {
		long l = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return LongTag.of(l);
	}

	@Override
	public byte getElementType() {
		return 4;
	}

	public void clear() {
		this.value = new long[0];
	}
}

package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Represents an NBT 64-bit integer array. This object is mutable and backed by
 * {@code long[]}. Its type is {@value NbtElement#LONG_ARRAY_TYPE}. Like Java arrays,
 * accessing indices that are out of bounds will throw {@link ArrayIndexOutOfBoundsException}.
 * The backing array can be obtained via {@link #getLongArray()}.
 */
public class NbtLongArray extends AbstractNbtList<NbtLong> {
	private static final int SIZE = 24;
	public static final NbtType<NbtLongArray> TYPE = new NbtType.OfVariableSize<NbtLongArray>() {
		public NbtLongArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(24L);
			int j = dataInput.readInt();
			nbtTagSizeTracker.add(8L * (long)j);
			long[] ls = new long[j];

			for (int k = 0; k < j; k++) {
				ls[k] = dataInput.readLong();
			}

			return new NbtLongArray(ls);
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
			int i = input.readInt();
			long[] ls = new long[i];

			for (int j = 0; j < i; j++) {
				ls[j] = input.readLong();
			}

			return visitor.visitLongArray(ls);
		}

		@Override
		public void skip(DataInput input) throws IOException {
			input.skipBytes(input.readInt() * 8);
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

	public NbtLongArray(long[] value) {
		this.value = value;
	}

	public NbtLongArray(LongSet value) {
		this.value = value.toLongArray();
	}

	public NbtLongArray(List<Long> value) {
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
	public int getSizeInBytes() {
		return 24 + 8 * this.value.length;
	}

	@Override
	public byte getType() {
		return NbtElement.LONG_ARRAY_TYPE;
	}

	@Override
	public NbtType<NbtLongArray> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public NbtLongArray copy() {
		long[] ls = new long[this.value.length];
		System.arraycopy(this.value, 0, ls, 0, this.value.length);
		return new NbtLongArray(ls);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtLongArray && Arrays.equals(this.value, ((NbtLongArray)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitLongArray(this);
	}

	/**
	 * {@return the underlying long array}
	 * 
	 * @apiNote This does not copy the array, so modifications to the returned array
	 * also apply to this NBT long array.
	 */
	public long[] getLongArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public NbtLong get(int i) {
		return NbtLong.of(this.value[i]);
	}

	public NbtLong method_10606(int i, NbtLong nbtLong) {
		long l = this.value[i];
		this.value[i] = nbtLong.longValue();
		return NbtLong.of(l);
	}

	public void add(int i, NbtLong nbtLong) {
		this.value = ArrayUtils.add(this.value, i, nbtLong.longValue());
	}

	@Override
	public boolean setElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value[index] = ((AbstractNbtNumber)element).longValue();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNbtNumber)element).longValue());
			return true;
		} else {
			return false;
		}
	}

	public NbtLong remove(int i) {
		long l = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return NbtLong.of(l);
	}

	@Override
	public byte getHeldType() {
		return NbtElement.LONG_TYPE;
	}

	public void clear() {
		this.value = new long[0];
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitLongArray(this.value);
	}
}

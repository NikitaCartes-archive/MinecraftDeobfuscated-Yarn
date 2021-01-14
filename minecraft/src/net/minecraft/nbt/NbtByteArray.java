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
 * Represents an NBT byte array.
 */
public class NbtByteArray extends AbstractNbtList<NbtByte> {
	public static final NbtType<NbtByteArray> TYPE = new NbtType<NbtByteArray>() {
		public NbtByteArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(192L);
			int j = dataInput.readInt();
			nbtTagSizeTracker.add(8L * (long)j);
			byte[] bs = new byte[j];
			dataInput.readFully(bs);
			return new NbtByteArray(bs);
		}

		@Override
		public String getCrashReportName() {
			return "BYTE[]";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Byte_Array";
		}
	};
	private byte[] value;

	public NbtByteArray(byte[] value) {
		this.value = value;
	}

	public NbtByteArray(List<Byte> value) {
		this(toArray(value));
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
	public byte getType() {
		return 7;
	}

	@Override
	public NbtType<NbtByteArray> getNbtType() {
		return TYPE;
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
	public NbtElement copy() {
		byte[] bs = new byte[this.value.length];
		System.arraycopy(this.value, 0, bs, 0, this.value.length);
		return new NbtByteArray(bs);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtByteArray && Arrays.equals(this.value, ((NbtByteArray)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text toText(String indent, int depth) {
		Text text = new LiteralText("B").formatted(RED);
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

	public byte[] getByteArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public NbtByte get(int i) {
		return NbtByte.of(this.value[i]);
	}

	public NbtByte set(int i, NbtByte nbtByte) {
		byte b = this.value[i];
		this.value[i] = nbtByte.byteValue();
		return NbtByte.of(b);
	}

	public void method_10531(int i, NbtByte nbtByte) {
		this.value = ArrayUtils.add(this.value, i, nbtByte.byteValue());
	}

	@Override
	public boolean setElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value[index] = ((AbstractNbtNumber)element).byteValue();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addElement(int index, NbtElement element) {
		if (element instanceof AbstractNbtNumber) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNbtNumber)element).byteValue());
			return true;
		} else {
			return false;
		}
	}

	public NbtByte method_10536(int i) {
		byte b = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return NbtByte.of(b);
	}

	@Override
	public byte getHeldType() {
		return 1;
	}

	public void clear() {
		this.value = new byte[0];
	}
}

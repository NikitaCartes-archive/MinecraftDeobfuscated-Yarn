package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Represents an NBT byte array.
 */
public class NbtByteArray extends AbstractNbtList<NbtByte> {
	private static final int SIZE = 192;
	public static final NbtType<NbtByteArray> TYPE = new NbtType.OfVariableSize<NbtByteArray>() {
		public NbtByteArray read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(192L);
			int j = dataInput.readInt();
			nbtTagSizeTracker.add(8L * (long)j);
			byte[] bs = new byte[j];
			dataInput.readFully(bs);
			return new NbtByteArray(bs);
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
			int i = input.readInt();
			byte[] bs = new byte[i];
			input.readFully(bs);
			return visitor.visitByteArray(bs);
		}

		@Override
		public void skip(DataInput input) throws IOException {
			input.skipBytes(input.readInt() * 1);
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
		return NbtElement.BYTE_ARRAY_TYPE;
	}

	@Override
	public NbtType<NbtByteArray> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
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
	public void accept(NbtElementVisitor visitor) {
		visitor.visitByteArray(this);
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
		return NbtElement.BYTE_TYPE;
	}

	public void clear() {
		this.value = new byte[0];
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitByteArray(this.value);
	}
}

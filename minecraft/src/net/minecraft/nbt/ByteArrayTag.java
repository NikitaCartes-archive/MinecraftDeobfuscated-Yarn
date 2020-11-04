package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.class_5627;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag extends AbstractListTag<ByteTag> {
	public static final TagReader<ByteArrayTag> READER = new TagReader<ByteArrayTag>() {
		public ByteArrayTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(192L);
			int j = dataInput.readInt();
			positionTracker.add(8L * (long)j);
			byte[] bs = new byte[j];
			dataInput.readFully(bs);
			return new ByteArrayTag(bs);
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

	public ByteArrayTag(byte[] value) {
		this.value = value;
	}

	public ByteArrayTag(List<Byte> value) {
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
	public TagReader<ByteArrayTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	@Override
	public Tag copy() {
		byte[] bs = new byte[this.value.length];
		System.arraycopy(this.value, 0, bs, 0, this.value.length);
		return new ByteArrayTag(bs);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public void method_32289(class_5627 arg) {
		arg.method_32290(this);
	}

	public byte[] getByteArray() {
		return this.value;
	}

	public int size() {
		return this.value.length;
	}

	public ByteTag get(int i) {
		return ByteTag.of(this.value[i]);
	}

	public ByteTag set(int i, ByteTag byteTag) {
		byte b = this.value[i];
		this.value[i] = byteTag.getByte();
		return ByteTag.of(b);
	}

	public void method_10531(int i, ByteTag byteTag) {
		this.value = ArrayUtils.add(this.value, i, byteTag.getByte());
	}

	@Override
	public boolean setTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value[index] = ((AbstractNumberTag)tag).getByte();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int index, Tag tag) {
		if (tag instanceof AbstractNumberTag) {
			this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getByte());
			return true;
		} else {
			return false;
		}
	}

	public ByteTag method_10536(int i) {
		byte b = this.value[i];
		this.value = ArrayUtils.remove(this.value, i);
		return ByteTag.of(b);
	}

	@Override
	public byte getElementType() {
		return 1;
	}

	public void clear() {
		this.value = new byte[0];
	}
}

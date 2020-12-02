package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class ListTag extends AbstractListTag<Tag> {
	public static final TagReader<ListTag> READER = new TagReader<ListTag>() {
		public ListTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
			positionTracker.add(296L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				byte b = dataInput.readByte();
				int j = dataInput.readInt();
				if (b == 0 && j > 0) {
					throw new RuntimeException("Missing type on ListTag");
				} else {
					positionTracker.add(32L * (long)j);
					TagReader<?> tagReader = TagReaders.of(b);
					List<Tag> list = Lists.<Tag>newArrayListWithCapacity(j);

					for (int k = 0; k < j; k++) {
						list.add(tagReader.read(dataInput, i + 1, positionTracker));
					}

					return new ListTag(list, b);
				}
			}
		}

		@Override
		public String getCrashReportName() {
			return "LIST";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_List";
		}
	};
	private final List<Tag> value;
	private byte type;

	private ListTag(List<Tag> list, byte type) {
		this.value = list;
		this.type = type;
	}

	public ListTag() {
		this(Lists.<Tag>newArrayList(), (byte)0);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		if (this.value.isEmpty()) {
			this.type = 0;
		} else {
			this.type = ((Tag)this.value.get(0)).getType();
		}

		output.writeByte(this.type);
		output.writeInt(this.value.size());

		for (Tag tag : this.value) {
			tag.write(output);
		}
	}

	@Override
	public byte getType() {
		return 9;
	}

	@Override
	public TagReader<ListTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	private void forgetTypeIfEmpty() {
		if (this.value.isEmpty()) {
			this.type = 0;
		}
	}

	@Override
	public Tag remove(int i) {
		Tag tag = (Tag)this.value.remove(i);
		this.forgetTypeIfEmpty();
		return tag;
	}

	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	public CompoundTag getCompound(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 10) {
				return (CompoundTag)tag;
			}
		}

		return new CompoundTag();
	}

	public ListTag getList(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 9) {
				return (ListTag)tag;
			}
		}

		return new ListTag();
	}

	public short getShort(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 2) {
				return ((ShortTag)tag).getShort();
			}
		}

		return 0;
	}

	public int getInt(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 3) {
				return ((IntTag)tag).getInt();
			}
		}

		return 0;
	}

	public int[] getIntArray(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 11) {
				return ((IntArrayTag)tag).getIntArray();
			}
		}

		return new int[0];
	}

	public double getDouble(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 6) {
				return ((DoubleTag)tag).getDouble();
			}
		}

		return 0.0;
	}

	public float getFloat(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			if (tag.getType() == 5) {
				return ((FloatTag)tag).getFloat();
			}
		}

		return 0.0F;
	}

	public String getString(int index) {
		if (index >= 0 && index < this.value.size()) {
			Tag tag = (Tag)this.value.get(index);
			return tag.getType() == 8 ? tag.asString() : tag.toString();
		} else {
			return "";
		}
	}

	public int size() {
		return this.value.size();
	}

	public Tag get(int i) {
		return (Tag)this.value.get(i);
	}

	@Override
	public Tag set(int i, Tag tag) {
		Tag tag2 = this.get(i);
		if (!this.setTag(i, tag)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
		} else {
			return tag2;
		}
	}

	@Override
	public void add(int i, Tag tag) {
		if (!this.addTag(i, tag)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
		}
	}

	@Override
	public boolean setTag(int index, Tag tag) {
		if (this.canAdd(tag)) {
			this.value.set(index, tag);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int index, Tag tag) {
		if (this.canAdd(tag)) {
			this.value.add(index, tag);
			return true;
		} else {
			return false;
		}
	}

	private boolean canAdd(Tag tag) {
		if (tag.getType() == 0) {
			return false;
		} else if (this.type == 0) {
			this.type = tag.getType();
			return true;
		} else {
			return this.type == tag.getType();
		}
	}

	public ListTag copy() {
		Iterable<Tag> iterable = (Iterable<Tag>)(TagReaders.of(this.type).isImmutable() ? this.value : Iterables.transform(this.value, Tag::copy));
		List<Tag> list = Lists.<Tag>newArrayList(iterable);
		return new ListTag(list, this.type);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public void accept(NbtTagVisitor visitor) {
		visitor.visitListTag(this);
	}

	@Override
	public byte getElementType() {
		return this.type;
	}

	public void clear() {
		this.value.clear();
		this.type = 0;
	}
}

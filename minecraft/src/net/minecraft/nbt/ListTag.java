package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ListTag extends AbstractListTag<Tag> {
	public static final TagReader<ListTag> READER = new TagReader<ListTag>() {
		public ListTag method_23249(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
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

	private ListTag(List<Tag> list, byte b) {
		this.value = list;
		this.type = b;
	}

	public ListTag() {
		this(Lists.<Tag>newArrayList(), (byte)0);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		if (this.value.isEmpty()) {
			this.type = 0;
		} else {
			this.type = ((Tag)this.value.get(0)).getType();
		}

		dataOutput.writeByte(this.type);
		dataOutput.writeInt(this.value.size());

		for (Tag tag : this.value) {
			tag.write(dataOutput);
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
		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < this.value.size(); i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value.get(i));
		}

		return stringBuilder.append(']').toString();
	}

	private void forgetTypeIfEmpty() {
		if (this.value.isEmpty()) {
			this.type = 0;
		}
	}

	@Override
	public Tag method_10536(int i) {
		Tag tag = (Tag)this.value.remove(i);
		this.forgetTypeIfEmpty();
		return tag;
	}

	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	public CompoundTag getCompound(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 10) {
				return (CompoundTag)tag;
			}
		}

		return new CompoundTag();
	}

	public ListTag getList(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 9) {
				return (ListTag)tag;
			}
		}

		return new ListTag();
	}

	public short getShort(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 2) {
				return ((ShortTag)tag).getShort();
			}
		}

		return 0;
	}

	public int getInt(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 3) {
				return ((IntTag)tag).getInt();
			}
		}

		return 0;
	}

	public int[] getIntArray(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 11) {
				return ((IntArrayTag)tag).getIntArray();
			}
		}

		return new int[0];
	}

	public double getDouble(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 6) {
				return ((DoubleTag)tag).getDouble();
			}
		}

		return 0.0;
	}

	public float getFloat(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 5) {
				return ((FloatTag)tag).getFloat();
			}
		}

		return 0.0F;
	}

	public String getString(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			return tag.getType() == 8 ? tag.asString() : tag.toString();
		} else {
			return "";
		}
	}

	public int size() {
		return this.value.size();
	}

	public Tag method_10534(int i) {
		return (Tag)this.value.get(i);
	}

	@Override
	public Tag method_10606(int i, Tag tag) {
		Tag tag2 = this.method_10534(i);
		if (!this.setTag(i, tag)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
		} else {
			return tag2;
		}
	}

	@Override
	public void method_10531(int i, Tag tag) {
		if (!this.addTag(i, tag)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
		}
	}

	@Override
	public boolean setTag(int i, Tag tag) {
		if (this.canAdd(tag)) {
			this.value.set(i, tag);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addTag(int i, Tag tag) {
		if (this.canAdd(tag)) {
			this.value.add(i, tag);
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

	public ListTag method_10612() {
		Iterable<Tag> iterable = (Iterable<Tag>)(TagReaders.of(this.type).isImmutable() ? this.value : Iterables.transform(this.value, Tag::copy));
		List<Tag> list = Lists.<Tag>newArrayList(iterable);
		return new ListTag(list, this.type);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof ListTag && Objects.equals(this.value, ((ListTag)object).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public Text toText(String string, int i) {
		if (this.isEmpty()) {
			return new LiteralText("[]");
		} else {
			Text text = new LiteralText("[");
			if (!string.isEmpty()) {
				text.append("\n");
			}

			for (int j = 0; j < this.value.size(); j++) {
				Text text2 = new LiteralText(Strings.repeat(string, i + 1));
				text2.append(((Tag)this.value.get(j)).toText(string, i + 1));
				if (j != this.value.size() - 1) {
					text2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
				}

				text.append(text2);
			}

			if (!string.isEmpty()) {
				text.append("\n").append(Strings.repeat(string, i));
			}

			text.append("]");
			return text;
		}
	}

	public int getElementType() {
		return this.type;
	}

	public void clear() {
		this.value.clear();
		this.type = 0;
	}
}

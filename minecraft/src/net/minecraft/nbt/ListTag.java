package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ListTag extends AbstractListTag<Tag> {
	private List<Tag> value = Lists.<Tag>newArrayList();
	private byte type = 0;

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
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(296L);
		if (depth > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.type = input.readByte();
			int i = input.readInt();
			if (this.type == 0 && i > 0) {
				throw new RuntimeException("Missing type on ListTag");
			} else {
				positionTracker.add(32L * (long)i);
				this.value = Lists.<Tag>newArrayListWithCapacity(i);

				for (int j = 0; j < i; j++) {
					Tag tag = Tag.createTag(this.type);
					tag.read(input, depth + 1, positionTracker);
					this.value.add(tag);
				}
			}
		}
	}

	@Override
	public byte getType() {
		return 9;
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

	public int getInt(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
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
		ListTag listTag = new ListTag();
		listTag.type = this.type;

		for (Tag tag : this.value) {
			Tag tag2 = tag.copy();
			listTag.value.add(tag2);
		}

		return listTag;
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public Text toText(String indent, int depth) {
		if (this.isEmpty()) {
			return new LiteralText("[]");
		} else {
			Text text = new LiteralText("[");
			if (!indent.isEmpty()) {
				text.append("\n");
			}

			for (int i = 0; i < this.value.size(); i++) {
				Text text2 = new LiteralText(Strings.repeat(indent, depth + 1));
				text2.append(((Tag)this.value.get(i)).toText(indent, depth + 1));
				if (i != this.value.size() - 1) {
					text2.append(String.valueOf(',')).append(indent.isEmpty() ? " " : "\n");
				}

				text.append(text2);
			}

			if (!indent.isEmpty()) {
				text.append("\n").append(Strings.repeat(indent, depth));
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

package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListTag extends AbstractListTag<Tag> {
	private static final Logger LOGGER = LogManager.getLogger();
	private List<Tag> value = Lists.<Tag>newArrayList();
	private byte type = 0;

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		if (this.value.isEmpty()) {
			this.type = 0;
		} else {
			this.type = ((Tag)this.value.get(0)).getType();
		}

		dataOutput.writeByte(this.type);
		dataOutput.writeInt(this.value.size());

		for (int i = 0; i < this.value.size(); i++) {
			((Tag)this.value.get(i)).write(dataOutput);
		}
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(296L);
		if (i > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.type = dataInput.readByte();
			int j = dataInput.readInt();
			if (this.type == 0 && j > 0) {
				throw new RuntimeException("Missing type on ListTag");
			} else {
				positionTracker.add(32L * (long)j);
				this.value = Lists.<Tag>newArrayListWithCapacity(j);

				for (int k = 0; k < j; k++) {
					Tag tag = Tag.createTag(this.type);
					tag.read(dataInput, i + 1, positionTracker);
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

	public boolean add(Tag tag) {
		if (this.canAdd(tag)) {
			this.value.add(tag);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Tag set(int i, Tag tag) {
		if (tag.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
			return (Tag)this.value.get(i);
		} else if (i >= 0 && i < this.value.size()) {
			if (this.type == 0) {
				this.type = tag.getType();
			} else if (this.type != tag.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return (Tag)this.value.get(i);
			}

			return (Tag)this.value.set(i, tag);
		} else {
			LOGGER.warn("index out of bounds to set tag in tag list");
			return null;
		}
	}

	public Tag remove(int i) {
		return (Tag)this.value.remove(i);
	}

	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	public CompoundTag getCompoundTag(int i) {
		if (i >= 0 && i < this.value.size()) {
			Tag tag = (Tag)this.value.get(i);
			if (tag.getType() == 10) {
				return (CompoundTag)tag;
			}
		}

		return new CompoundTag();
	}

	public ListTag getListTag(int i) {
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

	@Override
	public Tag get(int i) {
		return (Tag)(i >= 0 && i < this.value.size() ? (Tag)this.value.get(i) : new EndTag());
	}

	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public Tag getRaw(int i) {
		return (Tag)this.value.get(i);
	}

	@Override
	public void setRaw(int i, Tag tag) {
		this.value.set(i, tag);
	}

	@Override
	public void append(int i, Tag tag) {
		if (this.canAdd(tag)) {
			this.value.add(i, tag);
		}
	}

	private boolean canAdd(Tag tag) {
		if (tag.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
			return false;
		} else {
			if (this.type == 0) {
				this.type = tag.getType();
			} else if (this.type != tag.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return false;
			}

			return true;
		}
	}

	@Override
	public void remove(int i) {
		this.value.remove(i);
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

	public boolean equals(Object object) {
		return this == object ? true : object instanceof ListTag && Objects.equals(this.value, ((ListTag)object).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public TextComponent toTextComponent(String string, int i) {
		if (this.isEmpty()) {
			return new StringTextComponent("[]");
		} else {
			TextComponent textComponent = new StringTextComponent("[");
			if (!string.isEmpty()) {
				textComponent.append("\n");
			}

			for (int j = 0; j < this.value.size(); j++) {
				TextComponent textComponent2 = new StringTextComponent(Strings.repeat(string, i + 1));
				textComponent2.append(((Tag)this.value.get(j)).toTextComponent(string, i + 1));
				if (j != this.value.size() - 1) {
					textComponent2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
				}

				textComponent.append(textComponent2);
			}

			if (!string.isEmpty()) {
				textComponent.append("\n").append(Strings.repeat(string, i));
			}

			textComponent.append("]");
			return textComponent;
		}
	}

	public int getType() {
		return this.type;
	}

	public void clear() {
		this.value.clear();
	}
}

/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ListTag
extends AbstractListTag<Tag> {
    private List<Tag> value = Lists.newArrayList();
    private byte type = 0;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        this.type = this.value.isEmpty() ? (byte)0 : this.value.get(0).getType();
        dataOutput.writeByte(this.type);
        dataOutput.writeInt(this.value.size());
        for (Tag tag : this.value) {
            tag.write(dataOutput);
        }
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(296L);
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.type = dataInput.readByte();
        int j = dataInput.readInt();
        if (this.type == 0 && j > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        positionTracker.add(32L * (long)j);
        this.value = Lists.newArrayListWithCapacity(j);
        for (int k = 0; k < j; ++k) {
            Tag tag = Tag.createTag(this.type);
            tag.read(dataInput, i + 1, positionTracker);
            this.value.add(tag);
        }
    }

    @Override
    public byte getType() {
        return 9;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < this.value.size(); ++i) {
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
        Tag tag = this.value.remove(i);
        this.forgetTypeIfEmpty();
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public CompoundTag getCompoundTag(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 10) {
            return (CompoundTag)tag;
        }
        return new CompoundTag();
    }

    public ListTag getListTag(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 9) {
            return (ListTag)tag;
        }
        return new ListTag();
    }

    public short getShort(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 2) {
            return ((ShortTag)tag).getShort();
        }
        return 0;
    }

    public int getInt(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 3) {
            return ((IntTag)tag).getInt();
        }
        return 0;
    }

    public int[] getIntArray(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 11) {
            return ((IntArrayTag)tag).getIntArray();
        }
        return new int[0];
    }

    public double getDouble(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 6) {
            return ((DoubleTag)tag).getDouble();
        }
        return 0.0;
    }

    public float getFloat(int i) {
        Tag tag;
        if (i >= 0 && i < this.value.size() && (tag = this.value.get(i)).getType() == 5) {
            return ((FloatTag)tag).getFloat();
        }
        return 0.0f;
    }

    public String getString(int i) {
        if (i < 0 || i >= this.value.size()) {
            return "";
        }
        Tag tag = this.value.get(i);
        if (tag.getType() == 8) {
            return tag.asString();
        }
        return tag.toString();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    public Tag method_10534(int i) {
        return this.value.get(i);
    }

    @Override
    public Tag method_10606(int i, Tag tag) {
        Tag tag2 = this.method_10534(i);
        if (!this.setTag(i, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
        return tag2;
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
        }
        return false;
    }

    @Override
    public boolean addTag(int i, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.add(i, tag);
            return true;
        }
        return false;
    }

    private boolean canAdd(Tag tag) {
        if (tag.getType() == 0) {
            return false;
        }
        if (this.type == 0) {
            this.type = tag.getType();
            return true;
        }
        return this.type == tag.getType();
    }

    public ListTag method_10612() {
        ListTag listTag = new ListTag();
        listTag.type = this.type;
        for (Tag tag : this.value) {
            Tag tag2 = tag.copy();
            listTag.value.add(tag2);
        }
        return listTag;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ListTag && Objects.equals(this.value, ((ListTag)object).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public Component toTextComponent(String string, int i) {
        if (this.isEmpty()) {
            return new TextComponent("[]");
        }
        TextComponent component = new TextComponent("[");
        if (!string.isEmpty()) {
            component.append("\n");
        }
        for (int j = 0; j < this.value.size(); ++j) {
            TextComponent component2 = new TextComponent(Strings.repeat(string, i + 1));
            component2.append(this.value.get(j).toTextComponent(string, i + 1));
            if (j != this.value.size() - 1) {
                component2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
            }
            component.append(component2);
        }
        if (!string.isEmpty()) {
            component.append("\n").append(Strings.repeat(string, i));
        }
        component.append("]");
        return component;
    }

    public int getListType() {
        return this.type;
    }

    @Override
    public void clear() {
        this.value.clear();
        this.type = 0;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10612();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_10531(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.method_10606(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.method_10534(i);
    }
}


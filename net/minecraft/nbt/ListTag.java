/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
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
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.TagReaders;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class ListTag
extends AbstractListTag<Tag> {
    public static final TagReader<ListTag> READER = new TagReader<ListTag>(){

        @Override
        public ListTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(296L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            byte b = dataInput.readByte();
            int j = dataInput.readInt();
            if (b == 0 && j > 0) {
                throw new RuntimeException("Missing type on ListTag");
            }
            positionTracker.add(32L * (long)j);
            TagReader<?> tagReader = TagReaders.of(b);
            ArrayList<?> list = Lists.newArrayListWithCapacity(j);
            for (int k = 0; k < j; ++k) {
                list.add(tagReader.read(dataInput, i + 1, positionTracker));
            }
            return new ListTag(list, b);
        }

        @Override
        public String getCrashReportName() {
            return "LIST";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_List";
        }

        @Override
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final List<Tag> value;
    private byte type;

    private ListTag(List<Tag> list, byte type) {
        this.value = list;
        this.type = type;
    }

    public ListTag() {
        this(Lists.newArrayList(), 0);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        this.type = this.value.isEmpty() ? (byte)0 : this.value.get(0).getType();
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
        Tag tag = this.value.remove(i);
        this.forgetTypeIfEmpty();
        return tag;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public CompoundTag getCompound(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 10) {
            return (CompoundTag)tag;
        }
        return new CompoundTag();
    }

    public ListTag getList(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 9) {
            return (ListTag)tag;
        }
        return new ListTag();
    }

    public short getShort(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 2) {
            return ((ShortTag)tag).getShort();
        }
        return 0;
    }

    public int getInt(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 3) {
            return ((IntTag)tag).getInt();
        }
        return 0;
    }

    public int[] getIntArray(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 11) {
            return ((IntArrayTag)tag).getIntArray();
        }
        return new int[0];
    }

    public double getDouble(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 6) {
            return ((DoubleTag)tag).getDouble();
        }
        return 0.0;
    }

    public float getFloat(int index) {
        Tag tag;
        if (index >= 0 && index < this.value.size() && (tag = this.value.get(index)).getType() == 5) {
            return ((FloatTag)tag).getFloat();
        }
        return 0.0f;
    }

    public String getString(int index) {
        if (index < 0 || index >= this.value.size()) {
            return "";
        }
        Tag tag = this.value.get(index);
        if (tag.getType() == 8) {
            return tag.asString();
        }
        return tag.toString();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Tag get(int i) {
        return this.value.get(i);
    }

    @Override
    public Tag set(int i, Tag tag) {
        Tag tag2 = this.get(i);
        if (!this.setTag(i, tag)) {
            throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", tag.getType(), this.type));
        }
        return tag2;
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
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (this.canAdd(tag)) {
            this.value.add(index, tag);
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

    @Override
    public ListTag copy() {
        List<Tag> iterable = TagReaders.of(this.type).isImmutable() ? this.value : Iterables.transform(this.value, Tag::copy);
        ArrayList<Tag> list = Lists.newArrayList(iterable);
        return new ListTag(list, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ListTag && Objects.equals(this.value, ((ListTag)o).value);
    }

    @Override
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

    @Override
    public void clear() {
        this.value.clear();
        this.type = 0;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.remove(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.add(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (Tag)object);
    }

    @Override
    public /* synthetic */ Object get(int index) {
        return this.get(index);
    }
}


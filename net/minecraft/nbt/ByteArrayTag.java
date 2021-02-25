/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.NbtTagVisitor;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag
extends AbstractListTag<ByteTag> {
    public static final TagReader<ByteArrayTag> READER = new TagReader<ByteArrayTag>(){

        @Override
        public ByteArrayTag read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(192L);
            int j = dataInput.readInt();
            nbtTagSizeTracker.add(8L * (long)j);
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

        @Override
        public /* synthetic */ Tag read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private byte[] value;

    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    public ByteArrayTag(List<Byte> value) {
        this(ByteArrayTag.toArray(value));
    }

    private static byte[] toArray(List<Byte> list) {
        byte[] bs = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Byte byte_ = list.get(i);
            bs[i] = byte_ == null ? (byte)0 : byte_;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ByteArrayTag && Arrays.equals(this.value, ((ByteArrayTag)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitByteArrayTag(this);
    }

    public byte[] getByteArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public ByteTag get(int i) {
        return ByteTag.of(this.value[i]);
    }

    @Override
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
        }
        return false;
    }

    @Override
    public boolean addTag(int index, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, index, ((AbstractNumberTag)tag).getByte());
            return true;
        }
        return false;
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

    @Override
    public void clear() {
        this.value = new byte[0];
    }

    @Override
    public /* synthetic */ Tag remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Tag tag) {
        this.method_10531(i, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int i, Tag tag) {
        return this.set(i, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_10531(i, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}


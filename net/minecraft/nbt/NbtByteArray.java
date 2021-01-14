/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Represents an NBT byte array.
 */
public class NbtByteArray
extends AbstractNbtList<NbtByte> {
    public static final NbtType<NbtByteArray> TYPE = new NbtType<NbtByteArray>(){

        @Override
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

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private byte[] value;

    public NbtByteArray(byte[] value) {
        this.value = value;
    }

    public NbtByteArray(List<Byte> value) {
        this(NbtByteArray.toArray(value));
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

    public NbtType<NbtByteArray> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[B;");
        for (int i = 0; i < this.value.length; ++i) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtByteArray && Arrays.equals(this.value, ((NbtByteArray)o).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Text toText(String indent, int depth) {
        MutableText text = new LiteralText("B").formatted(RED);
        MutableText mutableText = new LiteralText("[").append(text).append(";");
        for (int i = 0; i < this.value.length; ++i) {
            MutableText mutableText2 = new LiteralText(String.valueOf(this.value[i])).formatted(GOLD);
            mutableText.append(" ").append(mutableText2).append(text);
            if (i == this.value.length - 1) continue;
            mutableText.append(",");
        }
        mutableText.append("]");
        return mutableText;
    }

    public byte[] getByteArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public NbtByte get(int i) {
        return NbtByte.of(this.value[i]);
    }

    @Override
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
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            this.value = ArrayUtils.add(this.value, index, ((AbstractNbtNumber)element).byteValue());
            return true;
        }
        return false;
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

    @Override
    public void clear() {
        this.value = new byte[0];
    }

    @Override
    public /* synthetic */ NbtElement remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, NbtElement nbtElement) {
        this.method_10531(i, (NbtByte)nbtElement);
    }

    @Override
    public /* synthetic */ NbtElement set(int i, NbtElement nbtElement) {
        return this.set(i, (NbtByte)nbtElement);
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_10536(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_10531(i, (NbtByte)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.set(i, (NbtByte)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.get(i);
    }
}


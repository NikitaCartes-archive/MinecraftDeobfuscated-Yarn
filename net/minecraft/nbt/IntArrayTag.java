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
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag
extends AbstractListTag<IntTag> {
    public static final TagReader<IntArrayTag> READER = new TagReader<IntArrayTag>(){

        public IntArrayTag method_23246(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(192L);
            int j = dataInput.readInt();
            positionTracker.add(32 * j);
            int[] is = new int[j];
            for (int k = 0; k < j; ++k) {
                is[k] = dataInput.readInt();
            }
            return new IntArrayTag(is);
        }

        @Override
        public String getCrashReportName() {
            return "INT[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Int_Array";
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            return this.method_23246(dataInput, i, positionTracker);
        }
    };
    private int[] value;

    public IntArrayTag(int[] is) {
        this.value = is;
    }

    public IntArrayTag(List<Integer> list) {
        this(IntArrayTag.toArray(list));
    }

    private static int[] toArray(List<Integer> list) {
        int[] is = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Integer integer = list.get(i);
            is[i] = integer == null ? 0 : integer;
        }
        return is;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        for (int i : this.value) {
            dataOutput.writeInt(i);
        }
    }

    @Override
    public byte getType() {
        return 11;
    }

    public TagReader<IntArrayTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[I;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]);
        }
        return stringBuilder.append(']').toString();
    }

    public IntArrayTag method_10591() {
        int[] is = new int[this.value.length];
        System.arraycopy(this.value, 0, is, 0, this.value.length);
        return new IntArrayTag(is);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof IntArrayTag && Arrays.equals(this.value, ((IntArrayTag)object).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    public int[] getIntArray() {
        return this.value;
    }

    @Override
    public Text toText(String string, int i) {
        Text text = new LiteralText("I").formatted(RED);
        Text text2 = new LiteralText("[").append(text).append(";");
        for (int j = 0; j < this.value.length; ++j) {
            text2.append(" ").append(new LiteralText(String.valueOf(this.value[j])).formatted(GOLD));
            if (j == this.value.length - 1) continue;
            text2.append(",");
        }
        text2.append("]");
        return text2;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    public IntTag method_10589(int i) {
        return IntTag.of(this.value[i]);
    }

    public IntTag method_17806(int i, IntTag intTag) {
        int j = this.value[i];
        this.value[i] = intTag.getInt();
        return IntTag.of(j);
    }

    public void method_17808(int i, IntTag intTag) {
        this.value = ArrayUtils.add(this.value, i, intTag.getInt());
    }

    @Override
    public boolean setTag(int i, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[i] = ((AbstractNumberTag)tag).getInt();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int i, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, i, ((AbstractNumberTag)tag).getInt());
            return true;
        }
        return false;
    }

    public IntTag method_17807(int i) {
        int j = this.value[i];
        this.value = ArrayUtils.remove(this.value, i);
        return IntTag.of(j);
    }

    @Override
    public void clear() {
        this.value = new int[0];
    }

    @Override
    public /* synthetic */ Tag method_10536(int i) {
        return this.method_17807(i);
    }

    @Override
    public /* synthetic */ void method_10531(int i, Tag tag) {
        this.method_17808(i, (IntTag)tag);
    }

    @Override
    public /* synthetic */ Tag method_10606(int i, Tag tag) {
        return this.method_17806(i, (IntTag)tag);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10591();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_17807(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_17808(i, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.method_17806(i, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.method_10589(i);
    }
}


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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag
extends AbstractListTag<IntTag> {
    private int[] value;

    IntArrayTag() {
    }

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
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        int j = dataInput.readInt();
        positionTracker.add(32 * j);
        this.value = new int[j];
        for (int k = 0; k < j; ++k) {
            this.value[k] = dataInput.readInt();
        }
    }

    @Override
    public byte getType() {
        return 11;
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
    public Component toTextComponent(String string, int i) {
        Component component = new TextComponent("I").applyFormat(RED);
        Component component2 = new TextComponent("[").append(component).append(";");
        for (int j = 0; j < this.value.length; ++j) {
            component2.append(" ").append(new TextComponent(String.valueOf(this.value[j])).applyFormat(GOLD));
            if (j == this.value.length - 1) continue;
            component2.append(",");
        }
        component2.append("]");
        return component2;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    public IntTag method_10589(int i) {
        return new IntTag(this.value[i]);
    }

    public IntTag method_17806(int i, IntTag intTag) {
        int j = this.value[i];
        this.value[i] = intTag.getInt();
        return new IntTag(j);
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
        return new IntTag(j);
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


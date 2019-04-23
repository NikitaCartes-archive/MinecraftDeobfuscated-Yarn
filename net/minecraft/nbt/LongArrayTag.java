/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag
extends AbstractListTag<LongTag> {
    private long[] value;

    LongArrayTag() {
    }

    public LongArrayTag(long[] ls) {
        this.value = ls;
    }

    public LongArrayTag(LongSet longSet) {
        this.value = longSet.toLongArray();
    }

    public LongArrayTag(List<Long> list) {
        this(LongArrayTag.toArray(list));
    }

    private static long[] toArray(List<Long> list) {
        long[] ls = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Long long_ = list.get(i);
            ls[i] = long_ == null ? 0L : long_;
        }
        return ls;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.value.length);
        for (long l : this.value) {
            dataOutput.writeLong(l);
        }
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(192L);
        int j = dataInput.readInt();
        positionTracker.add(64 * j);
        this.value = new long[j];
        for (int k = 0; k < j; ++k) {
            this.value[k] = dataInput.readLong();
        }
    }

    @Override
    public byte getType() {
        return 12;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[L;");
        for (int i = 0; i < this.value.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.value[i]).append('L');
        }
        return stringBuilder.append(']').toString();
    }

    public LongArrayTag method_10618() {
        long[] ls = new long[this.value.length];
        System.arraycopy(this.value, 0, ls, 0, this.value.length);
        return new LongArrayTag(ls);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongArrayTag && Arrays.equals(this.value, ((LongArrayTag)object).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public Component toTextComponent(String string, int i) {
        Component component = new TextComponent("L").applyFormat(RED);
        Component component2 = new TextComponent("[").append(component).append(";");
        for (int j = 0; j < this.value.length; ++j) {
            Component component3 = new TextComponent(String.valueOf(this.value[j])).applyFormat(GOLD);
            component2.append(" ").append(component3).append(component);
            if (j == this.value.length - 1) continue;
            component2.append(",");
        }
        component2.append("]");
        return component2;
    }

    public long[] getLongArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    public LongTag method_10616(int i) {
        return new LongTag(this.value[i]);
    }

    public LongTag method_17810(int i, LongTag longTag) {
        long l = this.value[i];
        this.value[i] = longTag.getLong();
        return new LongTag(l);
    }

    public void method_17812(int i, LongTag longTag) {
        this.value = ArrayUtils.add(this.value, i, longTag.getLong());
    }

    @Override
    public boolean setTag(int i, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value[i] = ((AbstractNumberTag)tag).getLong();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int i, Tag tag) {
        if (tag instanceof AbstractNumberTag) {
            this.value = ArrayUtils.add(this.value, i, ((AbstractNumberTag)tag).getLong());
            return true;
        }
        return false;
    }

    public LongTag method_17811(int i) {
        long l = this.value[i];
        this.value = ArrayUtils.remove(this.value, i);
        return new LongTag(l);
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public /* synthetic */ Tag method_10536(int i) {
        return this.method_17811(i);
    }

    @Override
    public /* synthetic */ void method_10531(int i, Tag tag) {
        this.method_17812(i, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag method_10606(int i, Tag tag) {
        return this.method_17810(i, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10618();
    }

    @Override
    public /* synthetic */ Object remove(int i) {
        return this.method_17811(i);
    }

    @Override
    public /* synthetic */ void add(int i, Object object) {
        this.method_17812(i, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object set(int i, Object object) {
        return this.method_17810(i, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object get(int i) {
        return this.method_10616(i);
    }
}


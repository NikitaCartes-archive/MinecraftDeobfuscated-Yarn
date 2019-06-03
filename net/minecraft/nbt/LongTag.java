/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class LongTag
extends AbstractNumberTag {
    private long value;

    LongTag() {
    }

    public LongTag(long l) {
        this.value = l;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(this.value);
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(128L);
        this.value = dataInput.readLong();
    }

    @Override
    public byte getType() {
        return 4;
    }

    @Override
    public String toString() {
        return this.value + "L";
    }

    public LongTag method_10621() {
        return new LongTag(this.value);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongTag && this.value == ((LongTag)object).value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public Text toText(String string, int i) {
        Text text = new LiteralText("L").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return this.value;
    }

    @Override
    public int getInt() {
        return (int)(this.value & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getShort() {
        return (short)(this.value & 0xFFFFL);
    }

    @Override
    public byte getByte() {
        return (byte)(this.value & 0xFFL);
    }

    @Override
    public double getDouble() {
        return this.value;
    }

    @Override
    public float getFloat() {
        return this.value;
    }

    @Override
    public Number getNumber() {
        return this.value;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10621();
    }
}


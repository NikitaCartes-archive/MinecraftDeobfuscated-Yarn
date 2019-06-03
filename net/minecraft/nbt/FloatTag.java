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
import net.minecraft.util.math.MathHelper;

public class FloatTag
extends AbstractNumberTag {
    private float value;

    FloatTag() {
    }

    public FloatTag(float f) {
        this.value = f;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(this.value);
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(96L);
        this.value = dataInput.readFloat();
    }

    @Override
    public byte getType() {
        return 5;
    }

    @Override
    public String toString() {
        return this.value + "f";
    }

    public FloatTag method_10587() {
        return new FloatTag(this.value);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof FloatTag && this.value == ((FloatTag)object).value;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public Text toText(String string, int i) {
        Text text = new LiteralText("f").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return (long)this.value;
    }

    @Override
    public int getInt() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
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
        return Float.valueOf(this.value);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10587();
    }
}


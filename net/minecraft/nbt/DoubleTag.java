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
import net.minecraft.nbt.TagReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DoubleTag
extends AbstractNumberTag {
    public static final DoubleTag ZERO = new DoubleTag(0.0);
    public static final TagReader<DoubleTag> READER = new TagReader<DoubleTag>(){

        public DoubleTag method_23242(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(128L);
            return DoubleTag.of(dataInput.readDouble());
        }

        @Override
        public String getCrashReportName() {
            return "DOUBLE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Double";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            return this.method_23242(dataInput, i, positionTracker);
        }
    };
    private final double value;

    private DoubleTag(double d) {
        this.value = d;
    }

    public static DoubleTag of(double d) {
        if (d == 0.0) {
            return ZERO;
        }
        return new DoubleTag(d);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(this.value);
    }

    @Override
    public byte getType() {
        return 6;
    }

    public TagReader<DoubleTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.value + "d";
    }

    public DoubleTag method_10585() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof DoubleTag && this.value == ((DoubleTag)object).value;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.value);
        return (int)(l ^ l >>> 32);
    }

    @Override
    public Text toText(String string, int i) {
        Text text = new LiteralText("d").formatted(RED);
        return new LiteralText(String.valueOf(this.value)).append(text).formatted(GOLD);
    }

    @Override
    public long getLong() {
        return (long)Math.floor(this.value);
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
        return (float)this.value;
    }

    @Override
    public Number getNumber() {
        return this.value;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10585();
    }
}


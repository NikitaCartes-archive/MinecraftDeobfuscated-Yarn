/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * Represents the NBT null value.
 * Defines the end of an NBT compound object,
 * represents nonexistent values in an NBT compound object,
 * and is the type of empty NBT lists.
 */
public class NbtNull
implements NbtElement {
    public static final NbtType<NbtNull> TYPE = new NbtType<NbtNull>(){

        @Override
        public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
            nbtTagSizeTracker.add(64L);
            return INSTANCE;
        }

        @Override
        public String getCrashReportName() {
            return "END";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_End";
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        @Override
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    public static final NbtNull INSTANCE = new NbtNull();

    private NbtNull() {
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public byte getType() {
        return 0;
    }

    public NbtType<NbtNull> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public NbtNull copy() {
        return this;
    }

    @Override
    public Text toText(String indent, int depth) {
        return LiteralText.EMPTY;
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}


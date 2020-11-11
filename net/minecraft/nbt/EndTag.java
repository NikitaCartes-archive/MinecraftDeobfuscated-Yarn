/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class EndTag
implements Tag {
    public static final TagReader<EndTag> READER = new TagReader<EndTag>(){

        @Override
        public EndTag read(DataInput dataInput, int i, PositionTracker positionTracker) {
            positionTracker.add(64L);
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
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public byte getType() {
        return 0;
    }

    public TagReader<EndTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public EndTag copy() {
        return this;
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitEndTag(this);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}


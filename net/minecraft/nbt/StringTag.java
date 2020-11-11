/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.visitor.NbtTagVisitor;

public class StringTag
implements Tag {
    public static final TagReader<StringTag> READER = new TagReader<StringTag>(){

        @Override
        public StringTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            positionTracker.add(288L);
            String string = dataInput.readUTF();
            positionTracker.add(16 * string.length());
            return StringTag.of(string);
        }

        @Override
        public String getCrashReportName() {
            return "STRING";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_String";
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
    private static final StringTag EMPTY = new StringTag("");
    private final String value;

    private StringTag(String value) {
        Objects.requireNonNull(value, "Null string not allowed");
        this.value = value;
    }

    public static StringTag of(String value) {
        if (value.isEmpty()) {
            return EMPTY;
        }
        return new StringTag(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public byte getType() {
        return 8;
    }

    public TagReader<StringTag> getReader() {
        return READER;
    }

    @Override
    public String toString() {
        return Tag.super.asString();
    }

    @Override
    public StringTag copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof StringTag && Objects.equals(this.value, ((StringTag)o).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitStringTag(this);
    }

    public static String escape(String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        int c = 0;
        for (int i = 0; i < value.length(); ++i) {
            int d = value.charAt(i);
            if (d == 92) {
                stringBuilder.append('\\');
            } else if (d == 34 || d == 39) {
                if (c == 0) {
                    int n = c = d == 34 ? 39 : 34;
                }
                if (c == d) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append((char)d);
        }
        if (c == 0) {
            c = 34;
        }
        stringBuilder.setCharAt(0, (char)c);
        stringBuilder.append((char)c);
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}


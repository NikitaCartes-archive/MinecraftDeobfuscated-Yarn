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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

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
        public /* synthetic */ Tag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            return this.read(dataInput, i, positionTracker);
        }
    };
    private static final StringTag EMPTY = new StringTag("");
    private final String value;

    private StringTag(String string) {
        Objects.requireNonNull(string, "Null string not allowed");
        this.value = string;
    }

    public static StringTag of(String string) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return new StringTag(string);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.value);
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
        return StringTag.escape(this.value);
    }

    @Override
    public StringTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof StringTag && Objects.equals(this.value, ((StringTag)object).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public Text toText(String string, int i) {
        String string2 = StringTag.escape(this.value);
        String string3 = string2.substring(0, 1);
        Text text = new LiteralText(string2.substring(1, string2.length() - 1)).formatted(GREEN);
        return new LiteralText(string3).append(text).append(string3);
    }

    public static String escape(String string) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        int c = 0;
        for (int i = 0; i < string.length(); ++i) {
            int d = string.charAt(i);
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


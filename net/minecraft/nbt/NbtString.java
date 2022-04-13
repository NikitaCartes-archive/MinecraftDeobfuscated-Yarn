/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents an NBT string.
 */
public class NbtString
implements NbtElement {
    private static final int SIZE = 288;
    public static final NbtType<NbtString> TYPE = new NbtType.OfVariableSize<NbtString>(){

        @Override
        public NbtString read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
            nbtTagSizeTracker.add(288L);
            String string = dataInput.readUTF();
            nbtTagSizeTracker.add(16 * string.length());
            return NbtString.of(string);
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
            return visitor.visitString(input.readUTF());
        }

        @Override
        public void skip(DataInput input) throws IOException {
            NbtString.skip(input);
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
        public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private static final NbtString EMPTY = new NbtString("");
    private static final char DOUBLE_QUOTE = '\"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char BACKSLASH = '\\';
    private static final char NULL = '\u0000';
    private final String value;

    public static void skip(DataInput input) throws IOException {
        input.skipBytes(input.readUnsignedShort());
    }

    private NbtString(String value) {
        Objects.requireNonNull(value, "Null string not allowed");
        this.value = value;
    }

    public static NbtString of(String value) {
        if (value.isEmpty()) {
            return EMPTY;
        }
        return new NbtString(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public byte getType() {
        return NbtElement.STRING_TYPE;
    }

    public NbtType<NbtString> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return NbtElement.super.asString();
    }

    @Override
    public NbtString copy() {
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtString && Objects.equals(this.value, ((NbtString)o).value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitString(this);
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
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitString(this.value);
    }

    @Override
    public /* synthetic */ NbtElement copy() {
        return this.copy();
    }
}


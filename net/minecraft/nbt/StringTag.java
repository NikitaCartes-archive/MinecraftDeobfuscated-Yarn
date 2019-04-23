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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class StringTag
implements Tag {
    private String value;

    public StringTag() {
        this("");
    }

    public StringTag(String string) {
        Objects.requireNonNull(string, "Null string not allowed");
        this.value = string;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.value);
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        positionTracker.add(288L);
        this.value = dataInput.readUTF();
        positionTracker.add(16 * this.value.length());
    }

    @Override
    public byte getType() {
        return 8;
    }

    @Override
    public String toString() {
        return StringTag.escape(this.value);
    }

    public StringTag method_10705() {
        return new StringTag(this.value);
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
    public Component toTextComponent(String string, int i) {
        String string2 = StringTag.escape(this.value);
        String string3 = string2.substring(0, 1);
        Component component = new TextComponent(string2.substring(1, string2.length() - 1)).applyFormat(GREEN);
        return new TextComponent(string3).append(component).append(string3);
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
        return this.method_10705();
    }
}


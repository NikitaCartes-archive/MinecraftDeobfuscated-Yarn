/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Tag {
    public static final Formatting AQUA = Formatting.AQUA;
    public static final Formatting GREEN = Formatting.GREEN;
    public static final Formatting GOLD = Formatting.GOLD;
    public static final Formatting RED = Formatting.RED;

    public void write(DataOutput var1) throws IOException;

    public String toString();

    public byte getType();

    public TagReader<?> getReader();

    public Tag copy();

    default public String asString() {
        return this.toString();
    }

    default public Text toText() {
        return this.toText("", 0);
    }

    public Text toText(String var1, int var2);
}


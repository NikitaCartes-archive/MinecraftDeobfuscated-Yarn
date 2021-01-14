/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
    public static final Formatting AQUA = Formatting.AQUA;
    public static final Formatting GREEN = Formatting.GREEN;
    public static final Formatting GOLD = Formatting.GOLD;
    public static final Formatting RED = Formatting.RED;

    public void write(DataOutput var1) throws IOException;

    public String toString();

    /**
     * Gets the type of this NBT element.
     * 
     * @return the type
     */
    public byte getType();

    /**
     * Gets the NBT type definition of this NBT element.
     * 
     * @return the element type definition
     */
    public NbtType<?> getNbtType();

    /**
     * Copies this NBT element.
     * 
     * @return the copied element
     */
    public NbtElement copy();

    default public String asString() {
        return this.toString();
    }

    default public Text toText() {
        return this.toText("", 0);
    }

    public Text toText(String var1, int var2);
}


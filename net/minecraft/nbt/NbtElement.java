/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

/**
 * Represents an NBT element.
 */
public interface NbtElement {
    public void write(DataOutput var1) throws IOException;

    public String toString();

    /**
     * Gets the type of this NBT element.
     * 
     * @return the type
     * 
     * @see net.fabricmc.yarn.constants.NbtTypeIds a list of valid types
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
        return new StringNbtWriter().apply(this);
    }

    public void accept(NbtElementVisitor var1);
}


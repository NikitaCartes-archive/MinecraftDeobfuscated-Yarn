/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.visitor;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

/**
 * A visitor interface for NBT elements.
 */
public interface NbtElementVisitor {
    public void visitString(NbtString var1);

    public void visitByte(NbtByte var1);

    public void visitShort(NbtShort var1);

    public void visitInt(NbtInt var1);

    public void visitLong(NbtLong var1);

    public void visitFloat(NbtFloat var1);

    public void visitDouble(NbtDouble var1);

    public void visitByteArray(NbtByteArray var1);

    public void visitIntArray(NbtIntArray var1);

    public void visitLongArray(NbtLongArray var1);

    public void visitList(NbtList var1);

    public void visitCompound(NbtCompound var1);

    public void visitNull(NbtNull var1);
}


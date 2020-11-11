/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.visitor;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;

/**
 * A visitor interface for NBT tags.
 */
public interface NbtTagVisitor {
    public void visitStringTag(StringTag var1);

    public void visitByteTag(ByteTag var1);

    public void visitShortTag(ShortTag var1);

    public void visitIntTag(IntTag var1);

    public void visitLongTag(LongTag var1);

    public void visitFloatTag(FloatTag var1);

    public void visitDoubleTag(DoubleTag var1);

    public void visitByteArrayTag(ByteArrayTag var1);

    public void visitIntArrayTag(IntArrayTag var1);

    public void visitLongArrayTag(LongArrayTag var1);

    public void visitListTag(ListTag var1);

    public void visitCompoundTag(CompoundTag var1);

    public void visitEndTag(EndTag var1);
}


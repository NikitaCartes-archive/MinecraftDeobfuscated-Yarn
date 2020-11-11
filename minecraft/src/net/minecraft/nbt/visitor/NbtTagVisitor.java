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
	void visitStringTag(StringTag tag);

	void visitByteTag(ByteTag tag);

	void visitShortTag(ShortTag tag);

	void visitIntTag(IntTag tag);

	void visitLongTag(LongTag tag);

	void visitFloatTag(FloatTag tag);

	void visitDoubleTag(DoubleTag tag);

	void visitByteArrayTag(ByteArrayTag tag);

	void visitIntArrayTag(IntArrayTag tag);

	void visitLongArrayTag(LongArrayTag tag);

	void visitListTag(ListTag tag);

	void visitCompoundTag(CompoundTag tag);

	void visitEndTag(EndTag tag);
}

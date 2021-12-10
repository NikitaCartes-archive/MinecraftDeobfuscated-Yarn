package net.minecraft.nbt.visitor;

import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

/**
 * A visitor interface for NBT elements.
 */
public interface NbtElementVisitor {
	void visitString(NbtString element);

	void visitByte(NbtByte element);

	void visitShort(NbtShort element);

	void visitInt(NbtInt element);

	void visitLong(NbtLong element);

	void visitFloat(NbtFloat element);

	void visitDouble(NbtDouble element);

	void visitByteArray(NbtByteArray element);

	void visitIntArray(NbtIntArray element);

	void visitLongArray(NbtLongArray element);

	void visitList(NbtList element);

	void visitCompound(NbtCompound compound);

	void visitEnd(NbtEnd element);
}

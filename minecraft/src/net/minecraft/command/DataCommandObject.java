package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

public interface DataCommandObject {
	void setTag(CompoundTag compoundTag) throws CommandSyntaxException;

	CompoundTag getTag() throws CommandSyntaxException;

	Text getModifiedFeedback();

	Text getQueryFeedback(Tag tag);

	Text getGetFeedback(NbtPathArgumentType.NbtPath nbtPath, double d, int i);
}

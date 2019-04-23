package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public interface DataCommandObject {
	void setTag(CompoundTag compoundTag) throws CommandSyntaxException;

	CompoundTag getTag() throws CommandSyntaxException;

	Component getModifiedFeedback();

	Component getQueryFeedback(Tag tag);

	Component getGetFeedback(NbtPathArgumentType.NbtPath nbtPath, double d, int i);
}

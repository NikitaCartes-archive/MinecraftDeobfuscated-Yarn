package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TextComponent;

public interface DataCommandObject {
	void setTag(CompoundTag compoundTag) throws CommandSyntaxException;

	CompoundTag getTag() throws CommandSyntaxException;

	TextComponent getModifiedFeedback();

	TextComponent getQueryFeedback(Tag tag);

	TextComponent getGetFeedback(NbtPathArgumentType.class_2209 arg, double d, int i);
}

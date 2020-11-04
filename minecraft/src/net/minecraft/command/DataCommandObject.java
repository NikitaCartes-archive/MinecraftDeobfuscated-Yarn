package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

public interface DataCommandObject {
	void setTag(CompoundTag tag) throws CommandSyntaxException;

	CompoundTag getTag() throws CommandSyntaxException;

	Text feedbackModify();

	Text feedbackQuery(Tag tag);

	Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result);
}

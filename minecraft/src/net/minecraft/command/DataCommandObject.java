package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public interface DataCommandObject {
	void setTag(NbtCompound tag) throws CommandSyntaxException;

	NbtCompound getTag() throws CommandSyntaxException;

	Text feedbackModify();

	Text feedbackQuery(NbtElement tag);

	Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result);
}

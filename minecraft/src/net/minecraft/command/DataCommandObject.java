package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public interface DataCommandObject {
	void setNbt(NbtCompound nbt) throws CommandSyntaxException;

	NbtCompound getNbt() throws CommandSyntaxException;

	Text feedbackModify();

	Text feedbackQuery(NbtElement element);

	Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result);
}

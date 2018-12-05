package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.TextComponent;

public interface class_3162 {
	void method_13880(CompoundTag compoundTag) throws CommandSyntaxException;

	CompoundTag method_13881() throws CommandSyntaxException;

	TextComponent method_13883();

	TextComponent method_13882(Tag tag);

	TextComponent method_13879(NbtPathArgumentType.class_2209 arg, double d, int i);
}

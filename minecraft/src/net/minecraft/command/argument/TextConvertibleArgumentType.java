package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.text.Text;

public interface TextConvertibleArgumentType<T> extends ArgumentType<T> {
	Text toText(T value);
}

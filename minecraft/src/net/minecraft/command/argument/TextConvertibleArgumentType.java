package net.minecraft.command.argument;

import net.minecraft.text.Text;

public interface TextConvertibleArgumentType<T> extends DecoratableArgumentType<T> {
	Text toText(T value);
}

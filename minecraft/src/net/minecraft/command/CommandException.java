package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;

public class CommandException extends RuntimeException {
	private final TextComponent field_9813;

	public CommandException(TextComponent textComponent) {
		super(textComponent.getText(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.field_9813 = textComponent;
	}

	public TextComponent method_9199() {
		return this.field_9813;
	}
}

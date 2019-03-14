package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.TextComponent;

public class CommandException extends RuntimeException {
	private final TextComponent message;

	public CommandException(TextComponent textComponent) {
		super(textComponent.getText(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.message = textComponent;
	}

	public TextComponent getMessageComponent() {
		return this.message;
	}
}

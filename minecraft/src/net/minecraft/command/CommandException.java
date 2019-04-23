package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;

public class CommandException extends RuntimeException {
	private final Component message;

	public CommandException(Component component) {
		super(component.getText(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.message = component;
	}

	public Component getMessageComponent() {
		return this.message;
	}
}

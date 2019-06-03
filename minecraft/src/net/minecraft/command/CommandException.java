package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.Text;

public class CommandException extends RuntimeException {
	private final Text field_9813;

	public CommandException(Text text) {
		super(text.asString(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.field_9813 = text;
	}

	public Text method_9199() {
		return this.field_9813;
	}
}

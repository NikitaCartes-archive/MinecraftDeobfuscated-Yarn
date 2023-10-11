package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface CommandAction<T> {
	void execute(CommandExecutionContext<T> context, int depth) throws CommandSyntaxException;
}

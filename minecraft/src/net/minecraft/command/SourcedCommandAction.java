package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface SourcedCommandAction<T> {
	void execute(T source, CommandExecutionContext<T> context, int depth) throws CommandSyntaxException;

	default CommandAction<T> bind(T source) {
		return (context, depth) -> this.execute(source, context, depth);
	}
}

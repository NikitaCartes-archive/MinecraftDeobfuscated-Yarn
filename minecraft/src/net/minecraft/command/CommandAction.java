package net.minecraft.command;

@FunctionalInterface
public interface CommandAction<T> {
	void execute(CommandExecutionContext<T> context, Frame frame);
}

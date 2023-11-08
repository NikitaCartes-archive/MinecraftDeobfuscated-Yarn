package net.minecraft.command;

public record CommandQueueEntry<T>(Frame frame, CommandAction<T> action) {
	public void execute(CommandExecutionContext<T> context) {
		this.action.execute(context, this.frame);
	}
}

package net.minecraft.command;

@FunctionalInterface
public interface SourcedCommandAction<T> {
	void execute(T source, CommandExecutionContext<T> context, int depth);

	default CommandAction<T> bind(T source) {
		return (context, depth) -> this.execute(source, context, depth);
	}
}

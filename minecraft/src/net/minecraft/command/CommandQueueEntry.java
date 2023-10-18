package net.minecraft.command;

import net.minecraft.server.function.Tracer;

public record CommandQueueEntry<T>(int depth, CommandAction<T> action) {
	public void execute(CommandExecutionContext<T> context) {
		Tracer tracer = context.getTracer();

		try {
			this.action.execute(context, this.depth);
		} catch (Exception var4) {
			if (tracer != null) {
				tracer.traceError(var4.getMessage());
			}
		}
	}
}

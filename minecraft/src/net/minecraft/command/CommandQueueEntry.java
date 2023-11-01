package net.minecraft.command;

import net.minecraft.server.function.Tracer;

public record CommandQueueEntry<T>(Frame frame, CommandAction<T> action) {
	public void execute(CommandExecutionContext<T> context) {
		Tracer tracer = context.getTracer();

		try {
			this.action.execute(context, this.frame);
		} catch (Exception var4) {
			if (tracer != null) {
				tracer.traceError(var4.getMessage());
			}
		}
	}
}

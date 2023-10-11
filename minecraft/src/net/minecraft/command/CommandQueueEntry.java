package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.function.Tracer;

public record CommandQueueEntry<T>(int depth, CommandAction<T> action) {
	public void execute(CommandExecutionContext<T> context) {
		Tracer tracer = context.getTracer();

		try {
			this.action.execute(context, this.depth);
		} catch (CommandSyntaxException var4) {
			if (tracer != null) {
				tracer.traceError(this.depth, var4.getRawMessage().getString());
			}
		} catch (Exception var5) {
			if (tracer != null) {
				tracer.traceError(this.depth, var5.getMessage());
			}
		}
	}
}

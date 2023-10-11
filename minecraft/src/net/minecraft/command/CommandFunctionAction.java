package net.minecraft.command;

import java.util.List;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.function.Tracer;

public class CommandFunctionAction<T> implements SourcedCommandAction<T> {
	private final Procedure<T> function;

	public CommandFunctionAction(Procedure<T> function) {
		this.function = function;
	}

	@Override
	public void execute(T object, CommandExecutionContext<T> commandExecutionContext, int i) {
		commandExecutionContext.decrementCommandQuota();
		List<SourcedCommandAction<T>> list = this.function.entries();
		Tracer tracer = commandExecutionContext.getTracer();
		if (tracer != null) {
			tracer.traceFunctionCall(i, this.function.id(), this.function.entries().size());
		}

		SteppedCommandAction.enqueueCommands(commandExecutionContext, i + 1, list, (depth, action) -> new CommandQueueEntry<>(depth, action.bind(object)));
	}
}

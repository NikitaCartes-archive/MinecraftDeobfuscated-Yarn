package net.minecraft.command;

import java.util.List;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.function.Tracer;

public class CommandFunctionAction<T extends AbstractServerCommandSource<T>> implements SourcedCommandAction<T> {
	private final Procedure<T> function;
	private final ReturnValueConsumer returnValueConsumer;
	private final boolean propagateReturn;

	public CommandFunctionAction(Procedure<T> function, ReturnValueConsumer returnValueConsumer, boolean propagateReturn) {
		this.function = function;
		this.returnValueConsumer = returnValueConsumer;
		this.propagateReturn = propagateReturn;
	}

	public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		commandExecutionContext.decrementCommandQuota();
		List<SourcedCommandAction<T>> list = this.function.entries();
		Tracer tracer = commandExecutionContext.getTracer();
		if (tracer != null) {
			tracer.traceFunctionCall(frame.depth(), this.function.id(), this.function.entries().size());
		}

		int i = frame.depth() + 1;
		Frame.Control control = this.propagateReturn ? frame.frameControl() : commandExecutionContext.getEscapeControl(i);
		Frame frame2 = new Frame(i, this.returnValueConsumer, control);
		SteppedCommandAction.enqueueCommands(
			commandExecutionContext, frame2, list, (framex, action) -> new CommandQueueEntry<>(framex, action.bind(abstractServerCommandSource))
		);
	}
}

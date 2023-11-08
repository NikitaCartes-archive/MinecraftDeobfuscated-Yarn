package net.minecraft.command;

import java.util.function.Consumer;
import net.minecraft.server.command.AbstractServerCommandSource;

public class IsolatedCommandAction<T extends AbstractServerCommandSource<T>> implements CommandAction<T> {
	private final Consumer<ExecutionControl<T>> controlConsumer;
	private final ReturnValueConsumer returnValueConsumer;

	public IsolatedCommandAction(Consumer<ExecutionControl<T>> controlConsumer, ReturnValueConsumer returnValueConsumer) {
		this.controlConsumer = controlConsumer;
		this.returnValueConsumer = returnValueConsumer;
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		int i = frame.depth() + 1;
		Frame frame2 = new Frame(i, this.returnValueConsumer, commandExecutionContext.getEscapeControl(i));
		this.controlConsumer.accept(ExecutionControl.of(commandExecutionContext, frame2));
	}
}

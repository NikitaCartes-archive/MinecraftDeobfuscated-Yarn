package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.command.AbstractServerCommandSource;

public class class_8940<T extends AbstractServerCommandSource<T>> implements CommandAction<T> {
	private final Consumer<ExecutionControl<T>> field_47170;
	private final ReturnValueConsumer field_47171;

	public class_8940(Consumer<ExecutionControl<T>> consumer, ReturnValueConsumer returnValueConsumer) {
		this.field_47170 = consumer;
		this.field_47171 = returnValueConsumer;
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		int i = frame.depth() + 1;
		Frame frame2 = new Frame(i, this.field_47171, commandExecutionContext.getEscapeControl(i));
		this.field_47170.accept(ExecutionControl.of(commandExecutionContext, frame2));
	}
}

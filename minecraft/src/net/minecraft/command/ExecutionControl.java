package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;

public interface ExecutionControl<T> {
	void enqueueAction(CommandAction<T> action);

	void setTracer(@Nullable Tracer tracer);

	@Nullable
	Tracer getTracer();

	Frame getFrame();

	static <T extends AbstractServerCommandSource<T>> ExecutionControl<T> of(CommandExecutionContext<T> context, Frame frame) {
		return new ExecutionControl<T>() {
			@Override
			public void enqueueAction(CommandAction<T> action) {
				context.enqueueCommand(new CommandQueueEntry<>(frame, action));
			}

			@Override
			public void setTracer(@Nullable Tracer tracer) {
				context.setTracer(tracer);
			}

			@Nullable
			@Override
			public Tracer getTracer() {
				return context.getTracer();
			}

			@Override
			public Frame getFrame() {
				return frame;
			}
		};
	}
}

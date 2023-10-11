package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.server.function.Tracer;

public interface ExecutionControl<T> {
	void enqueueAction(CommandAction<T> action);

	void doReturn();

	void setTracer(@Nullable Tracer tracer);

	@Nullable
	Tracer getTracer();
}

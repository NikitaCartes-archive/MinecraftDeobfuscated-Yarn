package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;

public interface ControlFlowAware<T> {
	void execute(T source, ContextChain<T> contextChain, ExecutionFlags flags, ExecutionControl<T> control);

	public interface Command<T> extends com.mojang.brigadier.Command<T>, ControlFlowAware<T> {
		@Override
		default int run(CommandContext<T> context) throws CommandSyntaxException {
			throw new UnsupportedOperationException("This function should not run");
		}
	}

	public abstract static class Helper<T extends AbstractServerCommandSource<T>> implements ControlFlowAware<T> {
		public final void execute(T abstractServerCommandSource, ContextChain<T> contextChain, ExecutionFlags executionFlags, ExecutionControl<T> executionControl) {
			try {
				this.executeInner(abstractServerCommandSource, contextChain, executionFlags, executionControl);
			} catch (CommandSyntaxException var6) {
				this.sendError(var6, abstractServerCommandSource, executionFlags, executionControl.getTracer());
				abstractServerCommandSource.getReturnValueConsumer().onFailure();
			}
		}

		protected void sendError(CommandSyntaxException exception, T source, ExecutionFlags flags, @Nullable Tracer tracer) {
			source.handleException(exception, flags.isSilent(), tracer);
		}

		protected abstract void executeInner(T source, ContextChain<T> contextChain, ExecutionFlags flags, ExecutionControl<T> control) throws CommandSyntaxException;
	}
}

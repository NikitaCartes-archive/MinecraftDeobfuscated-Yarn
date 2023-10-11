package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.AbstractServerCommandSource;

public interface ControlFlowAware<T> {
	void execute(T source, ContextChain<T> contextChain, boolean forkedMode, ExecutionControl<T> control) throws CommandSyntaxException;

	public interface Command<T> extends com.mojang.brigadier.Command<T>, ControlFlowAware<T> {
		@Override
		default int run(CommandContext<T> context) throws CommandSyntaxException {
			throw new UnsupportedOperationException("This function should not run");
		}
	}

	public abstract static class Helper<T extends AbstractServerCommandSource<T>> implements ControlFlowAware<T> {
		public final void execute(T abstractServerCommandSource, ContextChain<T> contextChain, boolean bl, ExecutionControl<T> executionControl) throws CommandSyntaxException {
			try {
				this.executeInner(abstractServerCommandSource, contextChain, bl, executionControl);
			} catch (CommandSyntaxException var6) {
				this.sendError(var6, abstractServerCommandSource, bl);
				abstractServerCommandSource.consumeResult(false, 0);
				throw var6;
			}
		}

		protected void sendError(CommandSyntaxException exception, T source, boolean silent) {
		}

		protected abstract void executeInner(T source, ContextChain<T> contextChain, boolean forkedMode, ExecutionControl<T> control) throws CommandSyntaxException;
	}
}

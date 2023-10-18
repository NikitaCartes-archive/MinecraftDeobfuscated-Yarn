package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Supplier;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;

public class FixedCommandAction<T extends AbstractServerCommandSource<T>> implements SourcedCommandAction<T> {
	private final String command;
	private final boolean forkedMode;
	private final CommandContext<T> context;

	public FixedCommandAction(String command, boolean forkedMode, CommandContext<T> context) {
		this.command = command;
		this.forkedMode = forkedMode;
		this.context = context;
	}

	public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, int i) {
		commandExecutionContext.getProfiler().push((Supplier<String>)(() -> "execute " + this.command));

		try {
			commandExecutionContext.decrementCommandQuota();
			int j = ContextChain.runExecutable(this.context, abstractServerCommandSource, AbstractServerCommandSource.asResultConsumer(), this.forkedMode);
			Tracer tracer = commandExecutionContext.getTracer();
			if (tracer != null) {
				tracer.traceCommandEnd(i, this.command, j);
			}
		} catch (CommandSyntaxException var9) {
			abstractServerCommandSource.handleException(var9, this.forkedMode, commandExecutionContext.getTracer());
		} finally {
			commandExecutionContext.getProfiler().pop();
		}
	}
}

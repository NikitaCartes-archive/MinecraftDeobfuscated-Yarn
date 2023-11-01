package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Supplier;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;

public class FixedCommandAction<T extends AbstractServerCommandSource<T>> implements SourcedCommandAction<T> {
	private final String command;
	private final ExecutionFlags flags;
	private final CommandContext<T> context;

	public FixedCommandAction(String command, ExecutionFlags flags, CommandContext<T> context) {
		this.command = command;
		this.flags = flags;
		this.context = context;
	}

	public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		commandExecutionContext.getProfiler().push((Supplier<String>)(() -> "execute " + this.command));

		try {
			commandExecutionContext.decrementCommandQuota();
			int i = ContextChain.runExecutable(this.context, abstractServerCommandSource, AbstractServerCommandSource.asResultConsumer(), this.flags.isSilent());
			Tracer tracer = commandExecutionContext.getTracer();
			if (tracer != null) {
				tracer.traceCommandEnd(frame.depth(), this.command, i);
			}
		} catch (CommandSyntaxException var9) {
			abstractServerCommandSource.handleException(var9, this.flags.isSilent(), commandExecutionContext.getTracer());
		} finally {
			commandExecutionContext.getProfiler().pop();
		}
	}
}

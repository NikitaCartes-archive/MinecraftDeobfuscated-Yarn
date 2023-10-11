package net.minecraft.command;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;
import net.minecraft.text.Text;

public class SingleCommandAction<T extends AbstractServerCommandSource<T>> {
	private static final DynamicCommandExceptionType FORK_LIMIT_EXCEPTION = new DynamicCommandExceptionType(
		count -> Text.stringifiedTranslatable("command.forkLimit", count)
	);
	private final String command;
	private final ContextChain<T> contextChain;

	public SingleCommandAction(String command, ContextChain<T> contextChain) {
		this.command = command;
		this.contextChain = contextChain;
	}

	protected void execute(List<T> forks, CommandExecutionContext<T> context, int depth, boolean silent) throws CommandSyntaxException {
		ContextChain<T> contextChain = this.contextChain;
		boolean bl = silent;
		List<T> list = forks;
		if (contextChain.getStage() != Stage.EXECUTE) {
			context.getProfiler().push((Supplier<String>)(() -> "prepare " + this.command));

			try {
				for (int i = context.getForkLimit(); contextChain.getStage() != Stage.EXECUTE; contextChain = contextChain.nextStage()) {
					CommandContext<T> commandContext = contextChain.getTopContext();
					bl |= commandContext.isForked();
					RedirectModifier<T> redirectModifier = commandContext.getRedirectModifier();
					if (redirectModifier instanceof Forkable<T> forkable) {
						forkable.execute(list, contextChain, bl, fixAtDepth(context, depth));
						return;
					}

					if (redirectModifier != null) {
						context.decrementCommandQuota();
						List<T> list2 = new ArrayList();

						for (T abstractServerCommandSource : list) {
							Collection<T> collection = ContextChain.runModifier(commandContext, abstractServerCommandSource, AbstractServerCommandSource.asResultConsumer(), bl);
							list2.addAll(collection);
							if (list2.size() >= i) {
								throw FORK_LIMIT_EXCEPTION.create(i);
							}
						}

						list = list2;
					}
				}
			} finally {
				context.getProfiler().pop();
			}
		}

		CommandContext<T> commandContext2 = contextChain.getTopContext();
		if (commandContext2.getCommand() instanceof ControlFlowAware<T> controlFlowAware) {
			ExecutionControl<T> executionControl = fixAtDepth(context, depth);

			for (T abstractServerCommandSourcex : list) {
				controlFlowAware.execute(abstractServerCommandSourcex, contextChain, bl, executionControl);
			}
		} else {
			FixedCommandAction<T> fixedCommandAction = new FixedCommandAction<>(this.command, bl, commandContext2);
			SteppedCommandAction.enqueueCommands(context, depth, list, (depthx, source) -> new CommandQueueEntry<>(depthx, fixedCommandAction.bind((T)source)));
		}
	}

	private static <T extends AbstractServerCommandSource<T>> ExecutionControl<T> fixAtDepth(CommandExecutionContext<T> context, int depth) {
		return new ExecutionControl<T>() {
			@Override
			public void enqueueAction(CommandAction<T> action) {
				context.enqueueCommand(new CommandQueueEntry<>(depth, action));
			}

			@Override
			public void doReturn() {
				context.escape(depth);
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
		};
	}

	protected void traceCommandStart(CommandExecutionContext<T> context, int depth) {
		Tracer tracer = context.getTracer();
		if (tracer != null) {
			tracer.traceCommandStart(depth, this.command);
		}
	}

	public String toString() {
		return this.command;
	}

	public static class MultiSource<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements CommandAction<T> {
		private final boolean forkedMode;
		private final List<T> sources;

		public MultiSource(String command, ContextChain<T> contextChain, boolean forkedMode, List<T> sources) {
			super(command, contextChain);
			this.forkedMode = forkedMode;
			this.sources = sources;
		}

		@Override
		public void execute(CommandExecutionContext<T> commandExecutionContext, int i) throws CommandSyntaxException {
			this.execute(this.sources, commandExecutionContext, i, this.forkedMode);
		}
	}

	public static class SingleSource<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements CommandAction<T> {
		private final T source;

		public SingleSource(String command, ContextChain<T> contextChain, T source) {
			super(command, contextChain);
			this.source = source;
		}

		@Override
		public void execute(CommandExecutionContext<T> commandExecutionContext, int i) throws CommandSyntaxException {
			this.traceCommandStart(commandExecutionContext, i);
			this.execute(List.of(this.source), commandExecutionContext, i, false);
		}
	}

	public static class Sourced<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements SourcedCommandAction<T> {
		public Sourced(String string, ContextChain<T> contextChain) {
			super(string, contextChain);
		}

		public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, int i) throws CommandSyntaxException {
			this.traceCommandStart(commandExecutionContext, i);
			this.execute(List.of(abstractServerCommandSource), commandExecutionContext, i, false);
		}
	}
}

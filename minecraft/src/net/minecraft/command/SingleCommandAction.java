package net.minecraft.command;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;
import net.minecraft.text.Text;

public class SingleCommandAction<T extends AbstractServerCommandSource<T>> {
	@VisibleForTesting
	public static final DynamicCommandExceptionType FORK_LIMIT_EXCEPTION = new DynamicCommandExceptionType(
		count -> Text.stringifiedTranslatable("command.forkLimit", count)
	);
	private final String command;
	private final ContextChain<T> contextChain;

	public SingleCommandAction(String command, ContextChain<T> contextChain) {
		this.command = command;
		this.contextChain = contextChain;
	}

	protected void execute(T baseSource, List<T> sources, CommandExecutionContext<T> context, Frame frame, ExecutionFlags flags) {
		ContextChain<T> contextChain = this.contextChain;
		ExecutionFlags executionFlags = flags;
		List<T> list = sources;
		if (contextChain.getStage() != Stage.EXECUTE) {
			context.getProfiler().push((Supplier<String>)(() -> "prepare " + this.command));

			try {
				for (int i = context.getForkLimit(); contextChain.getStage() != Stage.EXECUTE; contextChain = contextChain.nextStage()) {
					CommandContext<T> commandContext = contextChain.getTopContext();
					if (commandContext.isForked()) {
						executionFlags = executionFlags.setSilent();
					}

					RedirectModifier<T> redirectModifier = commandContext.getRedirectModifier();
					if (redirectModifier instanceof Forkable<T> forkable) {
						forkable.execute(baseSource, list, contextChain, executionFlags, ExecutionControl.of(context, frame));
						return;
					}

					if (redirectModifier != null) {
						context.decrementCommandQuota();
						boolean bl = executionFlags.isSilent();
						List<T> list2 = new ObjectArrayList<>();

						for (T abstractServerCommandSource : list) {
							try {
								Collection<T> collection = ContextChain.runModifier(commandContext, abstractServerCommandSource, (contextx, successful, returnValue) -> {
								}, bl);
								if (list2.size() + collection.size() >= i) {
									baseSource.handleException(FORK_LIMIT_EXCEPTION.create(i), bl, context.getTracer());
									return;
								}

								list2.addAll(collection);
							} catch (CommandSyntaxException var20) {
								abstractServerCommandSource.handleException(var20, bl, context.getTracer());
								if (!bl) {
									return;
								}
							}
						}

						list = list2;
					}
				}
			} finally {
				context.getProfiler().pop();
			}
		}

		if (list.isEmpty()) {
			if (executionFlags.isInsideReturnRun()) {
				context.enqueueCommand(new CommandQueueEntry<>(frame, FallthroughCommandAction.getInstance()));
			}
		} else {
			CommandContext<T> commandContext2 = contextChain.getTopContext();
			if (commandContext2.getCommand() instanceof ControlFlowAware<T> controlFlowAware) {
				ExecutionControl<T> executionControl = ExecutionControl.of(context, frame);

				for (T abstractServerCommandSource2 : list) {
					controlFlowAware.execute(abstractServerCommandSource2, contextChain, executionFlags, executionControl);
				}
			} else {
				if (executionFlags.isInsideReturnRun()) {
					T abstractServerCommandSource3 = (T)list.get(0);
					abstractServerCommandSource3 = abstractServerCommandSource3.withReturnValueConsumer(
						ReturnValueConsumer.chain(abstractServerCommandSource3.getReturnValueConsumer(), frame.returnValueConsumer())
					);
					list = List.of(abstractServerCommandSource3);
				}

				FixedCommandAction<T> fixedCommandAction = new FixedCommandAction<>(this.command, executionFlags, commandContext2);
				SteppedCommandAction.enqueueCommands(context, frame, list, (framex, source) -> new CommandQueueEntry<>(framex, fixedCommandAction.bind((T)source)));
			}
		}
	}

	protected void traceCommandStart(CommandExecutionContext<T> context, Frame frame) {
		Tracer tracer = context.getTracer();
		if (tracer != null) {
			tracer.traceCommandStart(frame.depth(), this.command);
		}
	}

	public String toString() {
		return this.command;
	}

	public static class MultiSource<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements CommandAction<T> {
		private final ExecutionFlags flags;
		private final T baseSource;
		private final List<T> sources;

		public MultiSource(String command, ContextChain<T> contextChain, ExecutionFlags flags, T baseSource, List<T> sources) {
			super(command, contextChain);
			this.baseSource = baseSource;
			this.sources = sources;
			this.flags = flags;
		}

		@Override
		public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
			this.execute(this.baseSource, this.sources, commandExecutionContext, frame, this.flags);
		}
	}

	public static class SingleSource<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements CommandAction<T> {
		private final T source;

		public SingleSource(String command, ContextChain<T> contextChain, T source) {
			super(command, contextChain);
			this.source = source;
		}

		@Override
		public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
			this.traceCommandStart(commandExecutionContext, frame);
			this.execute(this.source, List.of(this.source), commandExecutionContext, frame, ExecutionFlags.NONE);
		}
	}

	public static class Sourced<T extends AbstractServerCommandSource<T>> extends SingleCommandAction<T> implements SourcedCommandAction<T> {
		public Sourced(String string, ContextChain<T> contextChain) {
			super(string, contextChain);
		}

		public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, Frame frame) {
			this.traceCommandStart(commandExecutionContext, frame);
			this.execute(abstractServerCommandSource, List.of(abstractServerCommandSource), commandExecutionContext, frame, ExecutionFlags.NONE);
		}
	}
}

package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.function.Tracer;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public class CommandExecutionContext<T> implements AutoCloseable {
	private static final int MAX_COMMAND_QUEUE_LENGTH = 10000000;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final int maxCommandChainLength;
	private final int forkLimit;
	private final Profiler profiler;
	@Nullable
	private Tracer tracer;
	private int commandsRemaining;
	private boolean queueOverflowed;
	private final Deque<CommandQueueEntry<T>> commandQueue = Queues.<CommandQueueEntry<T>>newArrayDeque();
	private final List<CommandQueueEntry<T>> pendingCommands = Lists.<CommandQueueEntry<T>>newArrayList();

	public CommandExecutionContext(int maxCommandChainLength, int maxCommandForkCount, Profiler profiler) {
		this.maxCommandChainLength = maxCommandChainLength;
		this.forkLimit = maxCommandForkCount;
		this.profiler = profiler;
		this.commandsRemaining = maxCommandChainLength;
	}

	private static <T extends AbstractServerCommandSource<T>> Frame frame(CommandExecutionContext<T> context, ReturnValueConsumer returnValueConsumer) {
		return new Frame(0, returnValueConsumer, context.commandQueue::clear);
	}

	public static <T extends AbstractServerCommandSource<T>> void enqueueProcedureCall(
		CommandExecutionContext<T> context, Procedure<T> procedure, T source, ReturnValueConsumer returnValueConsumer
	) {
		context.enqueueCommand(
			new CommandQueueEntry<>(frame(context, returnValueConsumer), new CommandFunctionAction<>(procedure, source.getReturnValueConsumer(), false).bind(source))
		);
	}

	public static <T extends AbstractServerCommandSource<T>> void enqueueCommand(
		CommandExecutionContext<T> context, String command, ContextChain<T> contextChain, T source, ReturnValueConsumer returnValueConsumer
	) {
		context.enqueueCommand(new CommandQueueEntry<>(frame(context, returnValueConsumer), new SingleCommandAction.SingleSource<>(command, contextChain, source)));
	}

	private void markQueueOverflowed() {
		this.queueOverflowed = true;
		this.pendingCommands.clear();
		this.commandQueue.clear();
	}

	public void enqueueCommand(CommandQueueEntry<T> entry) {
		if (this.pendingCommands.size() + this.commandQueue.size() > 10000000) {
			this.markQueueOverflowed();
		}

		if (!this.queueOverflowed) {
			this.pendingCommands.add(entry);
		}
	}

	public void escape(int depth) {
		while (!this.commandQueue.isEmpty() && ((CommandQueueEntry)this.commandQueue.peek()).frame().depth() >= depth) {
			this.commandQueue.removeFirst();
		}
	}

	public Frame.Control getEscapeControl(int depth) {
		return () -> this.escape(depth);
	}

	public void run() {
		Lists.reverse(this.pendingCommands).forEach(this.commandQueue::addFirst);
		this.pendingCommands.clear();

		while (!this.commandQueue.isEmpty()) {
			if (this.commandsRemaining == 0) {
				LOGGER.info("Command execution stopped due to limit (executed {} commands)", this.maxCommandChainLength);
				break;
			}

			CommandQueueEntry<T> commandQueueEntry = (CommandQueueEntry<T>)this.commandQueue.removeFirst();
			commandQueueEntry.execute(this);
			if (this.queueOverflowed) {
				LOGGER.error("Command execution stopped due to command queue overflow (max {})", 10000000);
				break;
			}

			Lists.reverse(this.pendingCommands).forEach(this.commandQueue::addFirst);
			this.pendingCommands.clear();
		}
	}

	public void setTracer(@Nullable Tracer tracer) {
		this.tracer = tracer;
	}

	@Nullable
	public Tracer getTracer() {
		return this.tracer;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public int getForkLimit() {
		return this.forkLimit;
	}

	public void decrementCommandQuota() {
		this.commandsRemaining--;
	}

	public void close() {
		if (this.tracer != null) {
			this.tracer.close();
		}
	}
}

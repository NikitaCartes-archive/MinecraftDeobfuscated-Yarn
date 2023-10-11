package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;

public class SteppedCommandAction<T, P> implements CommandAction<T> {
	private final SteppedCommandAction.ActionWrapper<T, P> wrapper;
	private final List<P> actions;
	private final CommandQueueEntry<T> selfCommandQueueEntry;
	private int nextActionIndex;

	private SteppedCommandAction(SteppedCommandAction.ActionWrapper<T, P> wrapper, List<P> actions, int depth) {
		this.wrapper = wrapper;
		this.actions = actions;
		this.selfCommandQueueEntry = new CommandQueueEntry<>(depth, this);
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, int i) throws CommandSyntaxException {
		P object = (P)this.actions.get(this.nextActionIndex);
		commandExecutionContext.enqueueCommand(this.wrapper.create(i, object));
		if (++this.nextActionIndex < this.actions.size()) {
			commandExecutionContext.enqueueCommand(this.selfCommandQueueEntry);
		}
	}

	public static <T, P> void enqueueCommands(CommandExecutionContext<T> context, int depth, List<P> actions, SteppedCommandAction.ActionWrapper<T, P> wrapper) {
		int i = actions.size();
		if (i != 0) {
			if (i == 1) {
				context.enqueueCommand(wrapper.create(depth, (P)actions.get(0)));
			} else if (i == 2) {
				context.enqueueCommand(wrapper.create(depth, (P)actions.get(0)));
				context.enqueueCommand(wrapper.create(depth, (P)actions.get(1)));
			} else {
				context.enqueueCommand((new SteppedCommandAction<>(wrapper, actions, depth)).selfCommandQueueEntry);
			}
		}
	}

	@FunctionalInterface
	public interface ActionWrapper<T, P> {
		CommandQueueEntry<T> create(int depth, P action);
	}
}

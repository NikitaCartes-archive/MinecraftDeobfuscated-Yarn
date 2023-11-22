package net.minecraft.command;

import java.util.List;

public class SteppedCommandAction<T, P> implements CommandAction<T> {
	private final SteppedCommandAction.ActionWrapper<T, P> wrapper;
	private final List<P> actions;
	private final CommandQueueEntry<T> selfCommandQueueEntry;
	private int nextActionIndex;

	private SteppedCommandAction(SteppedCommandAction.ActionWrapper<T, P> wrapper, List<P> actions, Frame frame) {
		this.wrapper = wrapper;
		this.actions = actions;
		this.selfCommandQueueEntry = new CommandQueueEntry<>(frame, this);
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		P object = (P)this.actions.get(this.nextActionIndex);
		commandExecutionContext.enqueueCommand(this.wrapper.create(frame, object));
		if (++this.nextActionIndex < this.actions.size()) {
			commandExecutionContext.enqueueCommand(this.selfCommandQueueEntry);
		}
	}

	public static <T, P> void enqueueCommands(CommandExecutionContext<T> context, Frame frame, List<P> actions, SteppedCommandAction.ActionWrapper<T, P> wrapper) {
		int i = actions.size();
		switch (i) {
			case 0:
				break;
			case 1:
				context.enqueueCommand(wrapper.create(frame, (P)actions.get(0)));
				break;
			case 2:
				context.enqueueCommand(wrapper.create(frame, (P)actions.get(0)));
				context.enqueueCommand(wrapper.create(frame, (P)actions.get(1)));
				break;
			default:
				context.enqueueCommand((new SteppedCommandAction<>(wrapper, actions, frame)).selfCommandQueueEntry);
		}
	}

	@FunctionalInterface
	public interface ActionWrapper<T, P> {
		CommandQueueEntry<T> create(Frame frame, P action);
	}
}

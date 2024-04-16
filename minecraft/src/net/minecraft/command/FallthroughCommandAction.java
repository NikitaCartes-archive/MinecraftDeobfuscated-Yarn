package net.minecraft.command;

import net.minecraft.server.command.AbstractServerCommandSource;

public class FallthroughCommandAction<T extends AbstractServerCommandSource<T>> implements CommandAction<T> {
	private static final FallthroughCommandAction<? extends AbstractServerCommandSource<?>> INSTANCE = (FallthroughCommandAction<? extends AbstractServerCommandSource<?>>)(new FallthroughCommandAction<>());

	public static <T extends AbstractServerCommandSource<T>> CommandAction<T> getInstance() {
		return (CommandAction<T>)INSTANCE;
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		frame.fail();
		frame.doReturn();
	}
}

package net.minecraft;

import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.Frame;
import net.minecraft.server.command.AbstractServerCommandSource;

public class class_8939<T extends AbstractServerCommandSource<T>> implements CommandAction<T> {
	private static final class_8939<? extends AbstractServerCommandSource<?>> field_47169 = (class_8939<? extends AbstractServerCommandSource<?>>)(new class_8939<>());

	public static <T extends AbstractServerCommandSource<T>> CommandAction<T> method_54899() {
		return (CommandAction<T>)field_47169;
	}

	@Override
	public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
		frame.fail();
		frame.doReturn();
	}
}

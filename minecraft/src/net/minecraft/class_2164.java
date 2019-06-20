package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class class_2164 extends RuntimeException {
	private final class_2561 field_9813;

	public class_2164(class_2561 arg) {
		super(arg.method_10851(), null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
		this.field_9813 = arg;
	}

	public class_2561 method_9199() {
		return this.field_9813;
	}
}

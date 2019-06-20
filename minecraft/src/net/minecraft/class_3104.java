package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;

public class class_3104 {
	private static final SimpleCommandExceptionType field_13701 = new SimpleCommandExceptionType(new class_2588("commands.save.failed"));

	public static void method_13551(CommandDispatcher<class_2168> commandDispatcher) {
		commandDispatcher.register(
			class_2170.method_9247("save-all")
				.requires(arg -> arg.method_9259(4))
				.executes(commandContext -> method_13550(commandContext.getSource(), false))
				.then(class_2170.method_9247("flush").executes(commandContext -> method_13550(commandContext.getSource(), true)))
		);
	}

	private static int method_13550(class_2168 arg, boolean bl) throws CommandSyntaxException {
		arg.method_9226(new class_2588("commands.save.saving"), false);
		MinecraftServer minecraftServer = arg.method_9211();
		minecraftServer.method_3760().method_14617();
		boolean bl2 = minecraftServer.method_3723(true, bl, true);
		if (!bl2) {
			throw field_13701.create();
		} else {
			arg.method_9226(new class_2588("commands.save.success"), true);
			return 1;
		}
	}
}

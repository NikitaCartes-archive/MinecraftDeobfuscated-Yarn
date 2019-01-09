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
		boolean bl2 = false;
		minecraftServer.method_3760().method_14617();

		for (class_3218 lv : minecraftServer.method_3738()) {
			if (lv != null && method_13552(lv, bl)) {
				bl2 = true;
			}
		}

		if (!bl2) {
			throw field_13701.create();
		} else {
			arg.method_9226(new class_2588("commands.save.success"), true);
			return 1;
		}
	}

	private static boolean method_13552(class_3218 arg, boolean bl) {
		boolean bl2 = arg.field_13957;
		arg.field_13957 = false;

		boolean var4;
		try {
			arg.method_14176(null, bl);
			return true;
		} catch (class_1939 var8) {
			var4 = false;
		} finally {
			arg.field_13957 = bl2;
		}

		return var4;
	}
}

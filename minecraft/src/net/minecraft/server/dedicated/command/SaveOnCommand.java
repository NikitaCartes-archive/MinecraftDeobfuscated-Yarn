package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SaveOnCommand {
	private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.save.alreadyOn"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("save-on").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
			ServerCommandSource serverCommandSource = context.getSource();
			boolean bl = false;

			for (ServerWorld serverWorld : serverCommandSource.getServer().getWorlds()) {
				if (serverWorld != null && serverWorld.savingDisabled) {
					serverWorld.savingDisabled = false;
					bl = true;
				}
			}

			if (!bl) {
				throw ALREADY_ON_EXCEPTION.create();
			} else {
				serverCommandSource.sendFeedback(() -> Text.translatable("commands.save.enabled"), true);
				return 1;
			}
		}));
	}
}

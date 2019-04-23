package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class SaveOnCommand {
	private static final SimpleCommandExceptionType ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.alreadyOn"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("save-on").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				ServerCommandSource serverCommandSource = commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
					if (serverWorld != null && serverWorld.savingDisabled) {
						serverWorld.savingDisabled = false;
						bl = true;
					}
				}

				if (!bl) {
					throw ALREADY_ON_EXCEPTION.create();
				} else {
					serverCommandSource.sendFeedback(new TranslatableComponent("commands.save.enabled"), true);
					return 1;
				}
			})
		);
	}
}

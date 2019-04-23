package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class SaveOffCommand {
	private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.alreadyOff"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("save-off").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				ServerCommandSource serverCommandSource = commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
					if (serverWorld != null && !serverWorld.savingDisabled) {
						serverWorld.savingDisabled = true;
						bl = true;
					}
				}

				if (!bl) {
					throw ALREADY_OFF_EXCEPTION.create();
				} else {
					serverCommandSource.sendFeedback(new TranslatableComponent("commands.save.disabled"), true);
					return 1;
				}
			})
		);
	}
}

package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;

public class SaveOnCommand {
	private static final SimpleCommandExceptionType field_13704 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.save.alreadyOn"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("save-on").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				ServerCommandSource serverCommandSource = commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
					if (serverWorld != null && serverWorld.savingDisabled) {
						serverWorld.savingDisabled = false;
						bl = true;
					}
				}

				if (!bl) {
					throw field_13704.create();
				} else {
					serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.enabled"), true);
					return 1;
				}
			})
		);
	}
}

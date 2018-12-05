package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;

public class SaveOffCommand {
	private static final SimpleCommandExceptionType field_13703 = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.save.alreadyOff"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("save-off").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4)).executes(commandContext -> {
				ServerCommandSource serverCommandSource = commandContext.getSource();
				boolean bl = false;

				for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
					if (serverWorld != null && !serverWorld.field_13957) {
						serverWorld.field_13957 = true;
						bl = true;
					}
				}

				if (!bl) {
					throw field_13703.create();
				} else {
					serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.save.disabled"), true);
					return 1;
				}
			})
		);
	}
}

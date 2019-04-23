package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.chat.TranslatableComponent;

public class KillCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("kill")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.executes(commandContext -> execute(commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
		for (Entity entity : collection) {
			entity.kill();
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.kill.success.single", ((Entity)collection.iterator().next()).getDisplayName()), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.kill.success.multiple", collection.size()), true);
		}

		return collection.size();
	}
}

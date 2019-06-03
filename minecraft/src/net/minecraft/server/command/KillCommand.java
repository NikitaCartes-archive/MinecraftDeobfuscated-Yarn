package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

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
			serverCommandSource.method_9226(new TranslatableText("commands.kill.success.single", ((Entity)collection.iterator().next()).method_5476()), true);
		} else {
			serverCommandSource.method_9226(new TranslatableText("commands.kill.success.multiple", collection.size()), true);
		}

		return collection.size();
	}
}

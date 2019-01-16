package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableTextComponent;

public class KillCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("kill")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
						.executes(commandContext -> method_13430(commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets")))
				)
		);
	}

	private static int method_13430(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) {
		for (Entity entity : collection) {
			entity.kill();
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.kill.success.single", ((Entity)collection.iterator().next()).getDisplayName()), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.kill.success.multiple", collection.size()), true);
		}

		return collection.size();
	}
}

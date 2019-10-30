package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class KillCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("kill")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.executes(commandContext -> execute(commandContext.getSource(), ImmutableList.of(commandContext.getSource().getEntityOrThrow())))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.executes(commandContext -> execute(commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			entity.kill();
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.kill.success.single", ((Entity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.kill.success.multiple", targets.size()), true);
		}

		return targets.size();
	}
}

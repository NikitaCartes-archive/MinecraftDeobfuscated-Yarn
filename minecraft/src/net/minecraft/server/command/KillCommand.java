package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class KillCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("kill")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> execute(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow())))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.executes(context -> execute(context.getSource(), EntityArgumentType.getEntities(context, "targets")))
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets) {
		for (Entity entity : targets) {
			entity.kill();
		}

		if (targets.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.kill.success.single", ((Entity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.kill.success.multiple", targets.size()), true);
		}

		return targets.size();
	}
}

package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableText;

public class PublishCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed"));
	private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.publish.alreadyPublished", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("publish")
				.requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() && serverCommandSource.hasPermissionLevel(4))
				.executes(commandContext -> execute(commandContext.getSource(), NetworkUtils.findLocalPort()))
				.then(
					CommandManager.argument("port", IntegerArgumentType.integer(0, 65535))
						.executes(commandContext -> execute(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		if (serverCommandSource.getMinecraftServer().isRemote()) {
			throw ALREADY_PUBLISHED_EXCEPTION.create(serverCommandSource.getMinecraftServer().getServerPort());
		} else if (!serverCommandSource.getMinecraftServer().openToLan(serverCommandSource.getMinecraftServer().getDefaultGameMode(), false, i)) {
			throw FAILED_EXCEPTION.create();
		} else {
			serverCommandSource.method_9226(new TranslatableText("commands.publish.success", i), true);
			return i;
		}
	}
}

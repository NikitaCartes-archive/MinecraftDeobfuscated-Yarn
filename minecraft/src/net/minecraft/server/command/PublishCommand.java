package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableTextComponent;

public class PublishCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.publish.failed"));
	private static final DynamicCommandExceptionType ALREADYPUBLISHED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.publish.alreadyPublished", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("publish")
				.requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() && serverCommandSource.hasPermissionLevel(4))
				.executes(commandContext -> method_13509(commandContext.getSource(), NetworkUtils.findLocalPort()))
				.then(
					ServerCommandManager.argument("port", IntegerArgumentType.integer(0, 65535))
						.executes(commandContext -> method_13509(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
				)
		);
	}

	private static int method_13509(ServerCommandSource serverCommandSource, int i) throws CommandSyntaxException {
		if (serverCommandSource.getMinecraftServer().isRemote()) {
			throw ALREADYPUBLISHED_EXCEPTION.create(serverCommandSource.getMinecraftServer().getServerPort());
		} else if (!serverCommandSource.getMinecraftServer().openToLan(serverCommandSource.getMinecraftServer().getDefaultGameMode(), false, i)) {
			throw FAILED_EXCEPTION.create();
		} else {
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.publish.success", i), true);
			return i;
		}
	}
}

package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.class_7339;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

public class PublishCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.publish.failed"));
	private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(
		port -> new TranslatableText("commands.publish.alreadyPublished", port)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("publish")
				.requires(source -> source.hasPermissionLevel(4))
				.executes(context -> execute(context.getSource(), NetworkUtils.findLocalPort(), false, null))
				.then(
					CommandManager.argument("port", IntegerArgumentType.integer(0, 65535))
						.executes(context -> execute(context.getSource(), IntegerArgumentType.getInteger(context, "port"), false, null))
						.then(
							CommandManager.argument("allowCommands", BoolArgumentType.bool())
								.executes(
									commandContext -> execute(
											commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port"), BoolArgumentType.getBool(commandContext, "allowCommands"), null
										)
								)
								.then(
									CommandManager.argument("gamemode", class_7339.method_42960())
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													IntegerArgumentType.getInteger(commandContext, "port"),
													BoolArgumentType.getBool(commandContext, "allowCommands"),
													class_7339.method_42962(commandContext, "gamemode")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, int port, boolean bl, @Nullable GameMode gameMode) throws CommandSyntaxException {
		if (source.getServer().isRemote()) {
			throw ALREADY_PUBLISHED_EXCEPTION.create(source.getServer().getServerPort());
		} else if (!source.getServer().openToLan(gameMode, bl, port)) {
			throw FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(new TranslatableText("commands.publish.success", port), true);
			return port;
		}
	}
}

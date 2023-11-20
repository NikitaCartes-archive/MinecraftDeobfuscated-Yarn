package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.NetworkUtils;
import net.minecraft.world.GameMode;

public class PublishCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.publish.failed"));
	private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType(
		port -> Text.stringifiedTranslatable("commands.publish.alreadyPublished", port)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("publish")
				.requires(source -> source.hasPermissionLevel(4))
				.executes(context -> execute(context.getSource(), NetworkUtils.findLocalPort(), false, null))
				.then(
					CommandManager.argument("allowCommands", BoolArgumentType.bool())
						.executes(
							commandContext -> execute(commandContext.getSource(), NetworkUtils.findLocalPort(), BoolArgumentType.getBool(commandContext, "allowCommands"), null)
						)
						.then(
							CommandManager.argument("gamemode", GameModeArgumentType.gameMode())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											NetworkUtils.findLocalPort(),
											BoolArgumentType.getBool(commandContext, "allowCommands"),
											GameModeArgumentType.getGameMode(commandContext, "gamemode")
										)
								)
								.then(
									CommandManager.argument("port", IntegerArgumentType.integer(0, 65535))
										.executes(
											context -> execute(
													context.getSource(),
													IntegerArgumentType.getInteger(context, "port"),
													BoolArgumentType.getBool(context, "allowCommands"),
													GameModeArgumentType.getGameMode(context, "gamemode")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, int port, boolean allowCommands, @Nullable GameMode gameMode) throws CommandSyntaxException {
		if (source.getServer().isRemote()) {
			throw ALREADY_PUBLISHED_EXCEPTION.create(source.getServer().getServerPort());
		} else if (!source.getServer().openToLan(gameMode, allowCommands, port)) {
			throw FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(() -> getStartedText(port), true);
			return port;
		}
	}

	public static MutableText getStartedText(int port) {
		Text text = Texts.bracketedCopyable(String.valueOf(port));
		return Text.translatable("commands.publish.started", text);
	}
}

package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
	private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.difficulty.failure", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = ServerCommandManager.literal("difficulty");

		for (Difficulty difficulty : Difficulty.values()) {
			literalArgumentBuilder.then(
				ServerCommandManager.literal(difficulty.getTranslationKey()).executes(commandContext -> method_13173(commandContext.getSource(), difficulty))
			);
		}

		commandDispatcher.register(literalArgumentBuilder.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(commandContext -> {
			Difficulty difficultyx = commandContext.getSource().getWorld().getDifficulty();
			commandContext.getSource().sendFeedback(new TranslatableTextComponent("commands.difficulty.query", difficultyx.toTextComponent()), false);
			return difficultyx.getId();
		}));
	}

	public static int method_13173(ServerCommandSource serverCommandSource, Difficulty difficulty) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		if (minecraftServer.getWorld(DimensionType.field_13072).getDifficulty() == difficulty) {
			throw FAILURE_EXCEPTION.create(difficulty.getTranslationKey());
		} else {
			minecraftServer.setDifficulty(difficulty);
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.difficulty.success", difficulty.toTextComponent()), true);
			return 0;
		}
	}
}

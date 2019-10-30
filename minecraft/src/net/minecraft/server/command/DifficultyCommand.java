package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
	private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.difficulty.failure", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("difficulty");

		for (Difficulty difficulty : Difficulty.values()) {
			literalArgumentBuilder.then(CommandManager.literal(difficulty.getName()).executes(commandContext -> execute(commandContext.getSource(), difficulty)));
		}

		dispatcher.register(literalArgumentBuilder.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(commandContext -> {
			Difficulty difficultyx = commandContext.getSource().getWorld().getDifficulty();
			commandContext.getSource().sendFeedback(new TranslatableText("commands.difficulty.query", difficultyx.getTranslatableName()), false);
			return difficultyx.getId();
		}));
	}

	public static int execute(ServerCommandSource source, Difficulty difficulty) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getMinecraftServer();
		if (minecraftServer.getWorld(DimensionType.OVERWORLD).getDifficulty() == difficulty) {
			throw FAILURE_EXCEPTION.create(difficulty.getName());
		} else {
			minecraftServer.setDifficulty(difficulty, true);
			source.sendFeedback(new TranslatableText("commands.difficulty.success", difficulty.getTranslatableName()), true);
			return 0;
		}
	}
}

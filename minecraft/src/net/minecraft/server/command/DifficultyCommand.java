package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
	private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		difficulty -> Text.translatable("commands.difficulty.failure", difficulty)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("difficulty");

		for (Difficulty difficulty : Difficulty.values()) {
			literalArgumentBuilder.then(CommandManager.literal(difficulty.getName()).executes(context -> execute(context.getSource(), difficulty)));
		}

		dispatcher.register(literalArgumentBuilder.requires(source -> source.hasPermissionLevel(2)).executes(context -> {
			Difficulty difficultyx = context.getSource().getWorld().getDifficulty();
			context.getSource().sendFeedback(() -> Text.translatable("commands.difficulty.query", difficultyx.getTranslatableName()), false);
			return difficultyx.getId();
		}));
	}

	public static int execute(ServerCommandSource source, Difficulty difficulty) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (minecraftServer.getSaveProperties().getDifficulty() == difficulty) {
			throw FAILURE_EXCEPTION.create(difficulty.getName());
		} else {
			minecraftServer.setDifficulty(difficulty, true);
			source.sendFeedback(() -> Text.translatable("commands.difficulty.success", difficulty.getTranslatableName()), true);
			return 0;
		}
	}
}

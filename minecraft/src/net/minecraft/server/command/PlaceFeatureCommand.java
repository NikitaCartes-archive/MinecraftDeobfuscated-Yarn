package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class PlaceFeatureCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.placefeature.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("placefeature")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("feature", IdentifierArgumentType.identifier())
						.suggests(SuggestionProviders.AVAILABLE_FEATURES)
						.executes(
							context -> execute(
									context.getSource(), IdentifierArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(context.getSource().getPosition())
								)
						)
						.then(
							CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.executes(
									context -> execute(
											context.getSource(), IdentifierArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos")
										)
								)
						)
				)
		);
	}

	public static int execute(ServerCommandSource source, IdentifierArgumentType.RegistryEntry<ConfiguredFeature<?, ?>> configuredFeatureEntry, BlockPos pos) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		ConfiguredFeature<?, ?> configuredFeature = configuredFeatureEntry.resource();
		if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
			throw FAILED_EXCEPTION.create();
		} else {
			Identifier identifier = configuredFeatureEntry.id();
			source.sendFeedback(new TranslatableText("commands.placefeature.success", identifier, pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
	}
}

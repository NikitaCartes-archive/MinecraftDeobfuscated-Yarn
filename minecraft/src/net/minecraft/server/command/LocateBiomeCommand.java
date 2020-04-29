package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
	public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.locatebiome.invalid", object)
	);
	private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.locatebiome.notFound", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("locatebiome")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("biome", IdentifierArgumentType.identifier())
						.suggests(SuggestionProviders.ALL_BIOMES)
						.executes(commandContext -> execute(commandContext.getSource(), getBiome(commandContext, "biome")))
				)
		);
	}

	private static int execute(ServerCommandSource source, Biome biome) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = source.getWorld().locateBiome(biome, blockPos, 6400, 8);
		if (blockPos2 == null) {
			throw NOT_FOUND_EXCEPTION.create(biome.getName().getString());
		} else {
			return LocateCommand.sendCoordinates(source, biome.getName().getString(), blockPos, blockPos2, "commands.locatebiome.success");
		}
	}

	private static Biome getBiome(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(argumentName, Identifier.class);
		return (Biome)Registry.BIOME.getOrEmpty(identifier).orElseThrow(() -> INVALID_EXCEPTION.create(identifier));
	}
}

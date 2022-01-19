package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
	private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locatebiome.notFound", id)
	);
	private static final int RADIUS = 6400;
	private static final int BLOCK_CHECK_INTERVAL = 8;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("locatebiome")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("biome", IdentifierArgumentType.identifier())
						.suggests(SuggestionProviders.AVAILABLE_BIOMES)
						.executes(context -> execute(context.getSource(), IdentifierArgumentType.getBiomeEntry(context, "biome")))
				)
		);
	}

	private static int execute(ServerCommandSource source, IdentifierArgumentType.RegistryEntry<Biome> biomeEntry) throws CommandSyntaxException {
		Biome biome = biomeEntry.resource();
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = source.getWorld().locateBiome(biome, blockPos, 6400, 8);
		String string = biomeEntry.id().toString();
		if (blockPos2 == null) {
			throw NOT_FOUND_EXCEPTION.create(string);
		} else {
			return LocateCommand.sendCoordinates(source, string, blockPos, blockPos2, "commands.locatebiome.success");
		}
	}
}

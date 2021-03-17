package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
	public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locatebiome.invalid", id)
	);
	private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locatebiome.notFound", id)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("locatebiome")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("biome", IdentifierArgumentType.identifier())
						.suggests(SuggestionProviders.ALL_BIOMES)
						.executes(context -> execute(context.getSource(), context.getArgument("biome", Identifier.class)))
				)
		);
	}

	private static int execute(ServerCommandSource source, Identifier id) throws CommandSyntaxException {
		Biome biome = (Biome)source.getMinecraftServer().getRegistryManager().get(Registry.BIOME_KEY).getOrEmpty(id).orElseThrow(() -> INVALID_EXCEPTION.create(id));
		BlockPos blockPos = new BlockPos(source.getPosition());
		BlockPos blockPos2 = source.getWorld().locateBiome(biome, blockPos, 6400, 8);
		String string = id.toString();
		if (blockPos2 == null) {
			throw NOT_FOUND_EXCEPTION.create(string);
		} else {
			return LocateCommand.sendCoordinates(source, string, blockPos, blockPos2, "commands.locatebiome.success");
		}
	}
}

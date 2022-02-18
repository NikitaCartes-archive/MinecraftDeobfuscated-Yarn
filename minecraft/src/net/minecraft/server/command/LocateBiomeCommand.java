package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_7066;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
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
					CommandManager.argument("biome", class_7066.method_41170(Registry.BIOME_KEY))
						.executes(context -> execute(context.getSource(), class_7066.method_41165(context, "biome")))
				)
		);
	}

	private static int execute(ServerCommandSource source, class_7066.class_7068<Biome> arg) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(source.getPosition());
		Pair<BlockPos, RegistryEntry<Biome>> pair = source.getWorld().locateBiome(arg, blockPos, 6400, 8);
		if (pair == null) {
			throw NOT_FOUND_EXCEPTION.create(arg.method_41176());
		} else {
			return LocateCommand.sendCoordinates(source, arg, blockPos, pair, "commands.locatebiome.success");
		}
	}
}

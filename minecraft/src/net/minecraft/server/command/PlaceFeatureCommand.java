package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class PlaceFeatureCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.placefeature.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("placefeature")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("feature", RegistryKeyArgumentType.registryKey(Registry.CONFIGURED_FEATURE_KEY))
						.executes(
							context -> execute(
									context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(context.getSource().getPosition())
								)
						)
						.then(
							CommandManager.argument("pos", BlockPosArgumentType.blockPos())
								.executes(
									context -> execute(
											context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos")
										)
								)
						)
				)
		);
	}

	public static int execute(ServerCommandSource source, RegistryEntry<ConfiguredFeature<?, ?>> feature, BlockPos pos) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		ConfiguredFeature<?, ?> configuredFeature = feature.value();
		if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
			throw FAILED_EXCEPTION.create();
		} else {
			String string = (String)feature.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
			source.sendFeedback(Text.translatable("commands.placefeature.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
	}
}

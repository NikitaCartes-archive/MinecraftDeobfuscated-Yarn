package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class FillBiomeCommand {
	private static final int MAX_BLOCKS = 32768;
	public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.unloaded"));
	private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType(
		(maximum, specified) -> Text.translatable("commands.fillbiome.toobig", maximum, specified)
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("fillbiome")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("from", BlockPosArgumentType.blockPos())
						.then(
							CommandManager.argument("to", BlockPosArgumentType.blockPos())
								.then(
									CommandManager.argument("biome", RegistryEntryArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.BIOME))
										.executes(
											context -> execute(
													context.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(context, "from"),
													BlockPosArgumentType.getLoadedBlockPos(context, "to"),
													RegistryEntryArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME)
												)
										)
								)
						)
				)
		);
	}

	private static int convertCoordinate(int coordinate) {
		return BiomeCoords.toBlock(BiomeCoords.fromBlock(coordinate));
	}

	private static BlockPos convertPos(BlockPos pos) {
		return new BlockPos(convertCoordinate(pos.getX()), convertCoordinate(pos.getY()), convertCoordinate(pos.getZ()));
	}

	private static BiomeSupplier createBiomeSupplier(Chunk chunk, BlockBox box, RegistryEntry<Biome> biome) {
		return (x, y, z, noise) -> {
			int i = BiomeCoords.toBlock(x);
			int j = BiomeCoords.toBlock(y);
			int k = BiomeCoords.toBlock(z);
			return box.contains(i, j, k) ? biome : chunk.getBiomeForNoiseGen(x, y, z);
		};
	}

	private static int execute(ServerCommandSource source, BlockPos from, BlockPos to, RegistryEntry.Reference<Biome> biome) throws CommandSyntaxException {
		BlockPos blockPos = convertPos(from);
		BlockPos blockPos2 = convertPos(to);
		BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw TOO_BIG_EXCEPTION.create(32768, i);
		} else {
			ServerWorld serverWorld = source.getWorld();
			List<Chunk> list = new ArrayList();

			for (int j = ChunkSectionPos.getSectionCoord(blockBox.getMinZ()); j <= ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()); j++) {
				for (int k = ChunkSectionPos.getSectionCoord(blockBox.getMinX()); k <= ChunkSectionPos.getSectionCoord(blockBox.getMaxX()); k++) {
					Chunk chunk = serverWorld.getChunk(k, j, ChunkStatus.FULL, false);
					if (chunk == null) {
						throw UNLOADED_EXCEPTION.create();
					}

					list.add(chunk);
				}
			}

			for (Chunk chunk2 : list) {
				chunk2.populateBiomes(createBiomeSupplier(chunk2, blockBox, biome), serverWorld.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
				chunk2.setNeedsSaving(true);
				serverWorld.getChunkManager().threadedAnvilChunkStorage.sendChunkPacketToWatchingPlayers(chunk2);
			}

			source.sendFeedback(
				Text.translatable(
					"commands.fillbiome.success", blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()
				),
				true
			);
			return i;
		}
	}
}

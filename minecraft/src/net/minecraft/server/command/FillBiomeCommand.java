package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
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
													RegistryEntryArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME),
													registryEntry -> true
												)
										)
										.then(
											CommandManager.literal("replace")
												.then(
													CommandManager.argument("filter", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BIOME))
														.executes(
															context -> execute(
																	context.getSource(),
																	BlockPosArgumentType.getLoadedBlockPos(context, "from"),
																	BlockPosArgumentType.getLoadedBlockPos(context, "to"),
																	RegistryEntryArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME),
																	RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "filter", RegistryKeys.BIOME)::test
																)
														)
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

	private static BiomeSupplier createBiomeSupplier(
		MutableInt counter, Chunk chunk, BlockBox box, RegistryEntry<Biome> biome, Predicate<RegistryEntry<Biome>> filter
	) {
		return (x, y, z, noise) -> {
			int i = BiomeCoords.toBlock(x);
			int j = BiomeCoords.toBlock(y);
			int k = BiomeCoords.toBlock(z);
			RegistryEntry<Biome> registryEntry2 = chunk.getBiomeForNoiseGen(x, y, z);
			if (box.contains(i, j, k) && filter.test(registryEntry2)) {
				counter.increment();
				return biome;
			} else {
				return registryEntry2;
			}
		};
	}

	private static int execute(
		ServerCommandSource source, BlockPos from, BlockPos to, RegistryEntry.Reference<Biome> biome, Predicate<RegistryEntry<Biome>> filter
	) throws CommandSyntaxException {
		BlockPos blockPos = convertPos(from);
		BlockPos blockPos2 = convertPos(to);
		BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		int j = source.getWorld().getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT);
		if (i > j) {
			throw TOO_BIG_EXCEPTION.create(j, i);
		} else {
			ServerWorld serverWorld = source.getWorld();
			List<Chunk> list = new ArrayList();

			for (int k = ChunkSectionPos.getSectionCoord(blockBox.getMinZ()); k <= ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()); k++) {
				for (int l = ChunkSectionPos.getSectionCoord(blockBox.getMinX()); l <= ChunkSectionPos.getSectionCoord(blockBox.getMaxX()); l++) {
					Chunk chunk = serverWorld.getChunk(l, k, ChunkStatus.FULL, false);
					if (chunk == null) {
						throw UNLOADED_EXCEPTION.create();
					}

					list.add(chunk);
				}
			}

			MutableInt mutableInt = new MutableInt(0);

			for (Chunk chunk : list) {
				chunk.populateBiomes(createBiomeSupplier(mutableInt, chunk, blockBox, biome, filter), serverWorld.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
				chunk.setNeedsSaving(true);
			}

			serverWorld.getChunkManager().threadedAnvilChunkStorage.sendChunkBiomePackets(list);
			source.sendFeedback(
				() -> Text.translatable(
						"commands.fillbiome.success.count",
						mutableInt.getValue(),
						blockBox.getMinX(),
						blockBox.getMinY(),
						blockBox.getMinZ(),
						blockBox.getMaxX(),
						blockBox.getMaxY(),
						blockBox.getMaxZ()
					),
				true
			);
			return mutableInt.getValue();
		}
	}
}

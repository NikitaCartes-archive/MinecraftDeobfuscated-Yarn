package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
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
		(maximum, specified) -> Text.stringifiedTranslatable("commands.fillbiome.toobig", maximum, specified)
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
									CommandManager.argument("biome", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.BIOME))
										.executes(
											context -> execute(
													context.getSource(),
													BlockPosArgumentType.getLoadedBlockPos(context, "from"),
													BlockPosArgumentType.getLoadedBlockPos(context, "to"),
													RegistryEntryReferenceArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME),
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
																	RegistryEntryReferenceArgumentType.getRegistryEntry(context, "biome", RegistryKeys.BIOME),
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

	public static Either<Integer, CommandSyntaxException> fillBiome(ServerWorld world, BlockPos from, BlockPos to, RegistryEntry<Biome> biome) {
		return fillBiome(world, from, to, biome, biomex -> true, feedbackSupplier -> {
		});
	}

	public static Either<Integer, CommandSyntaxException> fillBiome(
		ServerWorld world, BlockPos from, BlockPos to, RegistryEntry<Biome> biome, Predicate<RegistryEntry<Biome>> filter, Consumer<Supplier<Text>> feedbackConsumer
	) {
		BlockPos blockPos = convertPos(from);
		BlockPos blockPos2 = convertPos(to);
		BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		int j = world.getGameRules().getInt(GameRules.COMMAND_MODIFICATION_BLOCK_LIMIT);
		if (i > j) {
			return Either.right(TOO_BIG_EXCEPTION.create(j, i));
		} else {
			List<Chunk> list = new ArrayList();

			for (int k = ChunkSectionPos.getSectionCoord(blockBox.getMinZ()); k <= ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()); k++) {
				for (int l = ChunkSectionPos.getSectionCoord(blockBox.getMinX()); l <= ChunkSectionPos.getSectionCoord(blockBox.getMaxX()); l++) {
					Chunk chunk = world.getChunk(l, k, ChunkStatus.FULL, false);
					if (chunk == null) {
						return Either.right(UNLOADED_EXCEPTION.create());
					}

					list.add(chunk);
				}
			}

			MutableInt mutableInt = new MutableInt(0);

			for (Chunk chunk : list) {
				chunk.populateBiomes(createBiomeSupplier(mutableInt, chunk, blockBox, biome, filter), world.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
				chunk.setNeedsSaving(true);
			}

			world.getChunkManager().chunkLoadingManager.sendChunkBiomePackets(list);
			feedbackConsumer.accept(
				(Supplier)() -> Text.translatable(
						"commands.fillbiome.success.count",
						mutableInt.getValue(),
						blockBox.getMinX(),
						blockBox.getMinY(),
						blockBox.getMinZ(),
						blockBox.getMaxX(),
						blockBox.getMaxY(),
						blockBox.getMaxZ()
					)
			);
			return Either.left(mutableInt.getValue());
		}
	}

	private static int execute(
		ServerCommandSource source, BlockPos from, BlockPos to, RegistryEntry.Reference<Biome> biome, Predicate<RegistryEntry<Biome>> filter
	) throws CommandSyntaxException {
		Either<Integer, CommandSyntaxException> either = fillBiome(
			source.getWorld(), from, to, biome, filter, feedbackSupplier -> source.sendFeedback(feedbackSupplier, true)
		);
		Optional<CommandSyntaxException> optional = either.right();
		if (optional.isPresent()) {
			throw (CommandSyntaxException)optional.get();
		} else {
			return (Integer)either.left().get();
		}
	}
}

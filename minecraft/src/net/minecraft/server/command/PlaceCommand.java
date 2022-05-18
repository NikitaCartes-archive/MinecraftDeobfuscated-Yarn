package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockMirrorArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockRotationArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.structure.StructureType;

public class PlaceCommand {
	private static final SimpleCommandExceptionType FEATURE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.feature.failed"));
	private static final SimpleCommandExceptionType JIGSAW_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.jigsaw.failed"));
	private static final SimpleCommandExceptionType STRUCTURE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		Text.translatable("commands.place.structure.failed")
	);
	private static final DynamicCommandExceptionType TEMPLATE_INVALID_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.translatable("commands.place.template.invalid", id)
	);
	private static final SimpleCommandExceptionType TEMPLATE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.template.failed"));
	private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		StructureManager structureManager = context.getSource().getWorld().getStructureManager();
		return CommandSource.suggestIdentifiers(structureManager.method_44226(), builder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("place")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("feature")
						.then(
							CommandManager.argument("feature", RegistryKeyArgumentType.registryKey(Registry.CONFIGURED_FEATURE_KEY))
								.executes(
									context -> executePlaceFeature(
											context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(context.getSource().getPosition())
										)
								)
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.executes(
											context -> executePlaceFeature(
													context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("jigsaw")
						.then(
							CommandManager.argument("pool", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_POOL_KEY))
								.then(
									CommandManager.argument("target", IdentifierArgumentType.identifier())
										.then(
											CommandManager.argument("max_depth", IntegerArgumentType.integer(1, 7))
												.executes(
													commandContext -> executePlaceJigsaw(
															commandContext.getSource(),
															RegistryKeyArgumentType.getStructurePoolEntry(commandContext, "pool"),
															IdentifierArgumentType.getIdentifier(commandContext, "target"),
															IntegerArgumentType.getInteger(commandContext, "max_depth"),
															new BlockPos(commandContext.getSource().getPosition())
														)
												)
												.then(
													CommandManager.argument("position", BlockPosArgumentType.blockPos())
														.executes(
															commandContext -> executePlaceJigsaw(
																	commandContext.getSource(),
																	RegistryKeyArgumentType.getStructurePoolEntry(commandContext, "pool"),
																	IdentifierArgumentType.getIdentifier(commandContext, "target"),
																	IntegerArgumentType.getInteger(commandContext, "max_depth"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "position")
																)
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("structure")
						.then(
							CommandManager.argument("structure", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_KEY))
								.executes(
									commandContext -> executePlaceStructure(
											commandContext.getSource(),
											RegistryKeyArgumentType.getStructureTypeEntry(commandContext, "structure"),
											new BlockPos(commandContext.getSource().getPosition())
										)
								)
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.executes(
											commandContext -> executePlaceStructure(
													commandContext.getSource(),
													RegistryKeyArgumentType.getStructureTypeEntry(commandContext, "structure"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("template")
						.then(
							CommandManager.argument("template", IdentifierArgumentType.identifier())
								.suggests(SUGGESTION_PROVIDER)
								.executes(
									commandContext -> executePlaceTemplate(
											commandContext.getSource(),
											IdentifierArgumentType.getIdentifier(commandContext, "template"),
											new BlockPos(commandContext.getSource().getPosition()),
											BlockRotation.NONE,
											BlockMirror.NONE,
											1.0F,
											0
										)
								)
								.then(
									CommandManager.argument("pos", BlockPosArgumentType.blockPos())
										.executes(
											commandContext -> executePlaceTemplate(
													commandContext.getSource(),
													IdentifierArgumentType.getIdentifier(commandContext, "template"),
													BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
													BlockRotation.NONE,
													BlockMirror.NONE,
													1.0F,
													0
												)
										)
										.then(
											CommandManager.argument("rotation", BlockRotationArgumentType.blockRotation())
												.executes(
													commandContext -> executePlaceTemplate(
															commandContext.getSource(),
															IdentifierArgumentType.getIdentifier(commandContext, "template"),
															BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
															BlockRotationArgumentType.getBlockRotation(commandContext, "rotation"),
															BlockMirror.NONE,
															1.0F,
															0
														)
												)
												.then(
													CommandManager.argument("mirror", BlockMirrorArgumentType.blockMirror())
														.executes(
															commandContext -> executePlaceTemplate(
																	commandContext.getSource(),
																	IdentifierArgumentType.getIdentifier(commandContext, "template"),
																	BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																	BlockRotationArgumentType.getBlockRotation(commandContext, "rotation"),
																	BlockMirrorArgumentType.getBlockMirror(commandContext, "mirror"),
																	1.0F,
																	0
																)
														)
														.then(
															CommandManager.argument("integrity", FloatArgumentType.floatArg(0.0F, 1.0F))
																.executes(
																	commandContext -> executePlaceTemplate(
																			commandContext.getSource(),
																			IdentifierArgumentType.getIdentifier(commandContext, "template"),
																			BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																			BlockRotationArgumentType.getBlockRotation(commandContext, "rotation"),
																			BlockMirrorArgumentType.getBlockMirror(commandContext, "mirror"),
																			FloatArgumentType.getFloat(commandContext, "integrity"),
																			0
																		)
																)
																.then(
																	CommandManager.argument("seed", IntegerArgumentType.integer())
																		.executes(
																			commandContext -> executePlaceTemplate(
																					commandContext.getSource(),
																					IdentifierArgumentType.getIdentifier(commandContext, "template"),
																					BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos"),
																					BlockRotationArgumentType.getBlockRotation(commandContext, "rotation"),
																					BlockMirrorArgumentType.getBlockMirror(commandContext, "mirror"),
																					FloatArgumentType.getFloat(commandContext, "integrity"),
																					IntegerArgumentType.getInteger(commandContext, "seed")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	public static int executePlaceFeature(ServerCommandSource source, RegistryEntry<ConfiguredFeature<?, ?>> feature, BlockPos pos) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		ConfiguredFeature<?, ?> configuredFeature = feature.value();
		ChunkPos chunkPos = new ChunkPos(pos);
		throwOnUnloadedPos(serverWorld, new ChunkPos(chunkPos.x - 1, chunkPos.z - 1), new ChunkPos(chunkPos.x + 1, chunkPos.z + 1));
		if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
			throw FEATURE_FAILED_EXCEPTION.create();
		} else {
			String string = (String)feature.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
			source.sendFeedback(Text.translatable("commands.place.feature.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
	}

	public static int executePlaceJigsaw(ServerCommandSource source, RegistryEntry<StructurePool> structurePool, Identifier id, int i, BlockPos pos) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		if (!StructurePoolBasedGenerator.generate(serverWorld, structurePool, id, i, pos, false)) {
			throw JIGSAW_FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(Text.translatable("commands.place.jigsaw.success", pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
	}

	public static int executePlaceStructure(ServerCommandSource source, RegistryEntry<StructureType> structureType, BlockPos pos) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		StructureType structureType2 = structureType.value();
		ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
		StructureStart structureStart = structureType2.createStructureStart(
			source.getRegistryManager(),
			chunkGenerator,
			chunkGenerator.getBiomeSource(),
			serverWorld.getChunkManager().getNoiseConfig(),
			serverWorld.getStructureManager(),
			serverWorld.getSeed(),
			new ChunkPos(pos),
			0,
			serverWorld,
			registryEntry -> true
		);
		if (!structureStart.hasChildren()) {
			throw STRUCTURE_FAILED_EXCEPTION.create();
		} else {
			BlockBox blockBox = structureStart.getBoundingBox();
			ChunkPos chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMinX()), ChunkSectionPos.getSectionCoord(blockBox.getMinZ()));
			ChunkPos chunkPos2 = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMaxX()), ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()));
			throwOnUnloadedPos(serverWorld, chunkPos, chunkPos2);
			ChunkPos.stream(chunkPos, chunkPos2)
				.forEach(
					chunkPosx -> structureStart.place(
							serverWorld,
							serverWorld.getStructureAccessor(),
							chunkGenerator,
							serverWorld.getRandom(),
							new BlockBox(chunkPosx.getStartX(), serverWorld.getBottomY(), chunkPosx.getStartZ(), chunkPosx.getEndX(), serverWorld.getTopY(), chunkPosx.getEndZ()),
							chunkPosx
						)
				);
			String string = (String)structureType.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
			source.sendFeedback(Text.translatable("commands.place.structure.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
			return 1;
		}
	}

	public static int executePlaceTemplate(
		ServerCommandSource source, Identifier id, BlockPos pos, BlockRotation rotation, BlockMirror mirror, float integrity, int seed
	) throws CommandSyntaxException {
		ServerWorld serverWorld = source.getWorld();
		StructureManager structureManager = serverWorld.getStructureManager();

		Optional<Structure> optional;
		try {
			optional = structureManager.getStructure(id);
		} catch (InvalidIdentifierException var13) {
			throw TEMPLATE_INVALID_EXCEPTION.create(id);
		}

		if (optional.isEmpty()) {
			throw TEMPLATE_INVALID_EXCEPTION.create(id);
		} else {
			Structure structure = (Structure)optional.get();
			throwOnUnloadedPos(serverWorld, new ChunkPos(pos), new ChunkPos(pos.add(structure.getSize())));
			StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(mirror).setRotation(rotation);
			if (integrity < 1.0F) {
				structurePlacementData.clearProcessors()
					.addProcessor(new BlockRotStructureProcessor(integrity))
					.setRandom(StructureBlockBlockEntity.createRandom((long)seed));
			}

			boolean bl = structure.place(serverWorld, pos, pos, structurePlacementData, StructureBlockBlockEntity.createRandom((long)seed), 2);
			if (!bl) {
				throw TEMPLATE_FAILED_EXCEPTION.create();
			} else {
				source.sendFeedback(Text.translatable("commands.place.template.success", id, pos.getX(), pos.getY(), pos.getZ()), true);
				return 1;
			}
		}
	}

	private static void throwOnUnloadedPos(ServerWorld world, ChunkPos pos1, ChunkPos pos2) throws CommandSyntaxException {
		if (ChunkPos.stream(pos1, pos2).filter(pos -> !world.canSetBlock(pos.getStartPos())).findAny().isPresent()) {
			throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
		}
	}
}

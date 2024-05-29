package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.PriorityIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class StructurePoolBasedGenerator {
	static final Logger LOGGER = LogUtils.getLogger();

	public static Optional<Structure.StructurePosition> generate(
		Structure.Context context,
		RegistryEntry<StructurePool> structurePool,
		Optional<Identifier> id,
		int size,
		BlockPos pos,
		boolean useExpansionHack,
		Optional<Heightmap.Type> projectStartToHeightmap,
		int maxDistanceFromCenter,
		StructurePoolAliasLookup aliasLookup,
		DimensionPadding dimensionPadding,
		StructureLiquidSettings liquidSettings
	) {
		DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
		HeightLimitView heightLimitView = context.world();
		ChunkRandom chunkRandom = context.random();
		Registry<StructurePool> registry = dynamicRegistryManager.get(RegistryKeys.TEMPLATE_POOL);
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		StructurePool structurePool2 = (StructurePool)structurePool.getKey()
			.flatMap(key -> registry.getOrEmpty(aliasLookup.lookup(key)))
			.orElse(structurePool.value());
		StructurePoolElement structurePoolElement = structurePool2.getRandomElement(chunkRandom);
		if (structurePoolElement == EmptyPoolElement.INSTANCE) {
			return Optional.empty();
		} else {
			BlockPos blockPos;
			if (id.isPresent()) {
				Identifier identifier = (Identifier)id.get();
				Optional<BlockPos> optional = findStartingJigsawPos(structurePoolElement, identifier, pos, blockRotation, structureTemplateManager, chunkRandom);
				if (optional.isEmpty()) {
					LOGGER.error(
						"No starting jigsaw {} found in start pool {}", identifier, structurePool.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>")
					);
					return Optional.empty();
				}

				blockPos = (BlockPos)optional.get();
			} else {
				blockPos = pos;
			}

			Vec3i vec3i = blockPos.subtract(pos);
			BlockPos blockPos2 = pos.subtract(vec3i);
			PoolStructurePiece poolStructurePiece = new PoolStructurePiece(
				structureTemplateManager,
				structurePoolElement,
				blockPos2,
				structurePoolElement.getGroundLevelDelta(),
				blockRotation,
				structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, blockRotation),
				liquidSettings
			);
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
			int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
			int k;
			if (projectStartToHeightmap.isPresent()) {
				k = pos.getY() + chunkGenerator.getHeightOnGround(i, j, (Heightmap.Type)projectStartToHeightmap.get(), heightLimitView, context.noiseConfig());
			} else {
				k = blockPos2.getY();
			}

			int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
			poolStructurePiece.translate(0, k - l, 0);
			int m = k + vec3i.getY();
			return Optional.of(
				new Structure.StructurePosition(
					new BlockPos(i, m, j),
					collector -> {
						List<PoolStructurePiece> list = Lists.<PoolStructurePiece>newArrayList();
						list.add(poolStructurePiece);
						if (size > 0) {
							Box box = new Box(
								(double)(i - maxDistanceFromCenter),
								(double)Math.max(m - maxDistanceFromCenter, heightLimitView.getBottomY() + dimensionPadding.bottom()),
								(double)(j - maxDistanceFromCenter),
								(double)(i + maxDistanceFromCenter + 1),
								(double)Math.min(m + maxDistanceFromCenter + 1, heightLimitView.getTopY() - dimensionPadding.top()),
								(double)(j + maxDistanceFromCenter + 1)
							);
							VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
							generate(
								context.noiseConfig(),
								size,
								useExpansionHack,
								chunkGenerator,
								structureTemplateManager,
								heightLimitView,
								chunkRandom,
								registry,
								poolStructurePiece,
								list,
								voxelShape,
								aliasLookup,
								liquidSettings
							);
							list.forEach(collector::addPiece);
						}
					}
				)
			);
		}
	}

	private static Optional<BlockPos> findStartingJigsawPos(
		StructurePoolElement pool, Identifier id, BlockPos pos, BlockRotation rotation, StructureTemplateManager structureManager, ChunkRandom random
	) {
		List<StructureTemplate.StructureBlockInfo> list = pool.getStructureBlockInfos(structureManager, pos, rotation, random);
		Optional<BlockPos> optional = Optional.empty();

		for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
			Identifier identifier = Identifier.tryParse(
				((NbtCompound)Objects.requireNonNull(structureBlockInfo.nbt(), () -> structureBlockInfo + " nbt was null")).getString("name")
			);
			if (id.equals(identifier)) {
				optional = Optional.of(structureBlockInfo.pos());
				break;
			}
		}

		return optional;
	}

	private static void generate(
		NoiseConfig noiseConfig,
		int maxSize,
		boolean modifyBoundingBox,
		ChunkGenerator chunkGenerator,
		StructureTemplateManager structureTemplateManager,
		HeightLimitView heightLimitView,
		Random random,
		Registry<StructurePool> structurePoolRegistry,
		PoolStructurePiece firstPiece,
		List<PoolStructurePiece> pieces,
		VoxelShape pieceShape,
		StructurePoolAliasLookup aliasLookup,
		StructureLiquidSettings liquidSettings
	) {
		StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(
			structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random
		);
		structurePoolGenerator.generatePiece(
			firstPiece, new MutableObject<>(pieceShape), 0, modifyBoundingBox, heightLimitView, noiseConfig, aliasLookup, liquidSettings
		);

		while (structurePoolGenerator.structurePieces.hasNext()) {
			StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = structurePoolGenerator.structurePieces.next();
			structurePoolGenerator.generatePiece(
				shapedPoolStructurePiece.piece,
				shapedPoolStructurePiece.pieceShape,
				shapedPoolStructurePiece.currentSize,
				modifyBoundingBox,
				heightLimitView,
				noiseConfig,
				aliasLookup,
				liquidSettings
			);
		}
	}

	public static boolean generate(ServerWorld world, RegistryEntry<StructurePool> structurePool, Identifier id, int size, BlockPos pos, boolean keepJigsaws) {
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
		StructureAccessor structureAccessor = world.getStructureAccessor();
		Random random = world.getRandom();
		Structure.Context context = new Structure.Context(
			world.getRegistryManager(),
			chunkGenerator,
			chunkGenerator.getBiomeSource(),
			world.getChunkManager().getNoiseConfig(),
			structureTemplateManager,
			world.getSeed(),
			new ChunkPos(pos),
			world,
			biome -> true
		);
		Optional<Structure.StructurePosition> optional = generate(
			context,
			structurePool,
			Optional.of(id),
			size,
			pos,
			false,
			Optional.empty(),
			128,
			StructurePoolAliasLookup.EMPTY,
			JigsawStructure.DEFAULT_DIMENSION_PADDING,
			JigsawStructure.DEFAULT_LIQUID_SETTINGS
		);
		if (optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = ((Structure.StructurePosition)optional.get()).generate();

			for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
				if (structurePiece instanceof PoolStructurePiece poolStructurePiece) {
					poolStructurePiece.generate(world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, keepJigsaws);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	static record ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
	}

	static final class StructurePoolGenerator {
		private final Registry<StructurePool> registry;
		private final int maxSize;
		private final ChunkGenerator chunkGenerator;
		private final StructureTemplateManager structureTemplateManager;
		private final List<? super PoolStructurePiece> children;
		private final Random random;
		final PriorityIterator<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = new PriorityIterator<>();

		StructurePoolGenerator(
			Registry<StructurePool> registry,
			int maxSize,
			ChunkGenerator chunkGenerator,
			StructureTemplateManager structureTemplateManager,
			List<? super PoolStructurePiece> children,
			Random random
		) {
			this.registry = registry;
			this.maxSize = maxSize;
			this.chunkGenerator = chunkGenerator;
			this.structureTemplateManager = structureTemplateManager;
			this.children = children;
			this.random = random;
		}

		void generatePiece(
			PoolStructurePiece piece,
			MutableObject<VoxelShape> pieceShape,
			int minY,
			boolean modifyBoundingBox,
			HeightLimitView world,
			NoiseConfig noiseConfig,
			StructurePoolAliasLookup aliasLookup,
			StructureLiquidSettings liquidSettings
		) {
			StructurePoolElement structurePoolElement = piece.getPoolElement();
			BlockPos blockPos = piece.getPos();
			BlockRotation blockRotation = piece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl = projection == StructurePool.Projection.RIGID;
			MutableObject<VoxelShape> mutableObject = new MutableObject<>();
			BlockBox blockBox = piece.getBoundingBox();
			int i = blockBox.getMinY();

			label134:
			for (StructureTemplate.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(
				this.structureTemplateManager, blockPos, blockRotation, this.random
			)) {
				Direction direction = JigsawBlock.getFacing(structureBlockInfo.state());
				BlockPos blockPos2 = structureBlockInfo.pos();
				BlockPos blockPos3 = blockPos2.offset(direction);
				int j = blockPos2.getY() - i;
				int k = -1;
				RegistryKey<StructurePool> registryKey = lookupPool(structureBlockInfo, aliasLookup);
				Optional<? extends RegistryEntry<StructurePool>> optional = this.registry.getEntry(registryKey);
				if (optional.isEmpty()) {
					StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent pool: {}", registryKey.getValue());
				} else {
					RegistryEntry<StructurePool> registryEntry = (RegistryEntry<StructurePool>)optional.get();
					if (registryEntry.value().getElementCount() == 0 && !registryEntry.matchesKey(StructurePools.EMPTY)) {
						StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent pool: {}", registryKey.getValue());
					} else {
						RegistryEntry<StructurePool> registryEntry2 = registryEntry.value().getFallback();
						if (registryEntry2.value().getElementCount() == 0 && !registryEntry2.matchesKey(StructurePools.EMPTY)) {
							StructurePoolBasedGenerator.LOGGER
								.warn("Empty or non-existent fallback pool: {}", registryEntry2.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>"));
						} else {
							boolean bl2 = blockBox.contains(blockPos3);
							MutableObject<VoxelShape> mutableObject2;
							if (bl2) {
								mutableObject2 = mutableObject;
								if (mutableObject.getValue() == null) {
									mutableObject.setValue(VoxelShapes.cuboid(Box.from(blockBox)));
								}
							} else {
								mutableObject2 = pieceShape;
							}

							List<StructurePoolElement> list = Lists.<StructurePoolElement>newArrayList();
							if (minY != this.maxSize) {
								list.addAll(registryEntry.value().getElementIndicesInRandomOrder(this.random));
							}

							list.addAll(registryEntry2.value().getElementIndicesInRandomOrder(this.random));
							int l = structureBlockInfo.nbt() != null ? structureBlockInfo.nbt().getInt("placement_priority") : 0;

							for (StructurePoolElement structurePoolElement2 : list) {
								if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
									break;
								}

								for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
									List<StructureTemplate.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(
										this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2, this.random
									);
									BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2);
									int m;
									if (modifyBoundingBox && blockBox2.getBlockCountY() <= 16) {
										m = list2.stream().mapToInt(structureBlockInfox -> {
											if (!blockBox2.contains(structureBlockInfox.pos().offset(JigsawBlock.getFacing(structureBlockInfox.state())))) {
												return 0;
											} else {
												RegistryKey<StructurePool> registryKeyx = lookupPool(structureBlockInfox, aliasLookup);
												Optional<? extends RegistryEntry<StructurePool>> optionalx = this.registry.getEntry(registryKeyx);
												Optional<RegistryEntry<StructurePool>> optional2 = optionalx.map(entry -> ((StructurePool)entry.value()).getFallback());
												int ix = (Integer)optionalx.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
												int jx = (Integer)optional2.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
												return Math.max(ix, jx);
											}
										}).max().orElse(0);
									} else {
										m = 0;
									}

									for (StructureTemplate.StructureBlockInfo structureBlockInfo2 : list2) {
										if (JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) {
											BlockPos blockPos4 = structureBlockInfo2.pos();
											BlockPos blockPos5 = blockPos3.subtract(blockPos4);
											BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, blockPos5, blockRotation2);
											int n = blockBox3.getMinY();
											StructurePool.Projection projection2 = structurePoolElement2.getProjection();
											boolean bl3 = projection2 == StructurePool.Projection.RIGID;
											int o = blockPos4.getY();
											int p = j - o + JigsawBlock.getFacing(structureBlockInfo.state()).getOffsetY();
											int q;
											if (bl && bl3) {
												q = i + p;
											} else {
												if (k == -1) {
													k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
												}

												q = k - o;
											}

											int r = q - n;
											BlockBox blockBox4 = blockBox3.offset(0, r, 0);
											BlockPos blockPos6 = blockPos5.add(0, r, 0);
											if (m > 0) {
												int s = Math.max(m + 1, blockBox4.getMaxY() - blockBox4.getMinY());
												blockBox4.encompass(new BlockPos(blockBox4.getMinX(), blockBox4.getMinY() + s, blockBox4.getMinZ()));
											}

											if (!VoxelShapes.matchesAnywhere(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) {
												mutableObject2.setValue(VoxelShapes.combine(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
												int s = piece.getGroundLevelDelta();
												int t;
												if (bl3) {
													t = s - p;
												} else {
													t = structurePoolElement2.getGroundLevelDelta();
												}

												PoolStructurePiece poolStructurePiece = new PoolStructurePiece(
													this.structureTemplateManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4, liquidSettings
												);
												int u;
												if (bl) {
													u = i + j;
												} else if (bl3) {
													u = q + o;
												} else {
													if (k == -1) {
														k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
													}

													u = k + p / 2;
												}

												piece.addJunction(new JigsawJunction(blockPos3.getX(), u - j + s, blockPos3.getZ(), p, projection2));
												poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), u - o + t, blockPos2.getZ(), -p, projection));
												this.children.add(poolStructurePiece);
												if (minY + 1 <= this.maxSize) {
													StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = new StructurePoolBasedGenerator.ShapedPoolStructurePiece(
														poolStructurePiece, mutableObject2, minY + 1
													);
													this.structurePieces.enqueue(shapedPoolStructurePiece, l);
												}
												continue label134;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		private static RegistryKey<StructurePool> lookupPool(StructureTemplate.StructureBlockInfo structureBlockInfo, StructurePoolAliasLookup aliasLookup) {
			NbtCompound nbtCompound = (NbtCompound)Objects.requireNonNull(structureBlockInfo.nbt(), () -> structureBlockInfo + " nbt was null");
			RegistryKey<StructurePool> registryKey = StructurePools.of(nbtCompound.getString("pool"));
			return aliasLookup.lookup(registryKey);
		}
	}
}

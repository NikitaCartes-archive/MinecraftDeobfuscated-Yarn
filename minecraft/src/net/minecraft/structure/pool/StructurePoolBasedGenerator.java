package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class StructurePoolBasedGenerator {
	static final Logger LOGGER = LogUtils.getLogger();

	public static Optional<StructureType.StructurePosition> generate(
		StructureType.Context context,
		RegistryEntry<StructurePool> registryEntry,
		Optional<Identifier> optional,
		int i,
		BlockPos blockPos,
		boolean bl,
		Optional<Heightmap.Type> optional2,
		int j
	) {
		DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
		ChunkGenerator chunkGenerator = context.chunkGenerator();
		StructureManager structureManager = context.structureManager();
		HeightLimitView heightLimitView = context.world();
		ChunkRandom chunkRandom = context.random();
		Registry<StructurePool> registry = dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY);
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		StructurePool structurePool = registryEntry.value();
		StructurePoolElement structurePoolElement = structurePool.getRandomElement(chunkRandom);
		if (structurePoolElement == EmptyPoolElement.INSTANCE) {
			return Optional.empty();
		} else {
			BlockPos blockPos2;
			if (optional.isPresent()) {
				Identifier identifier = (Identifier)optional.get();
				Optional<BlockPos> optional3 = method_43566(structurePoolElement, identifier, blockPos, blockRotation, structureManager, chunkRandom);
				if (optional3.isEmpty()) {
					LOGGER.error("No starting jigsaw {} found in start pool {}", identifier, ((RegistryKey)registryEntry.getKey().get()).getValue());
					return Optional.empty();
				}

				blockPos2 = (BlockPos)optional3.get();
			} else {
				blockPos2 = blockPos;
			}

			Vec3i vec3i = blockPos2.subtract(blockPos);
			BlockPos blockPos3 = blockPos.subtract(vec3i);
			PoolStructurePiece poolStructurePiece = new PoolStructurePiece(
				structureManager,
				structurePoolElement,
				blockPos3,
				structurePoolElement.getGroundLevelDelta(),
				blockRotation,
				structurePoolElement.getBoundingBox(structureManager, blockPos3, blockRotation)
			);
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int k = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
			int l = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
			int m;
			if (optional2.isPresent()) {
				m = blockPos.getY() + chunkGenerator.getHeightOnGround(k, l, (Heightmap.Type)optional2.get(), heightLimitView, context.noiseConfig());
			} else {
				m = blockPos3.getY();
			}

			int n = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
			poolStructurePiece.translate(0, m - n, 0);
			int o = m + vec3i.getY();
			return Optional.of(new StructureType.StructurePosition(new BlockPos(k, o, l), structurePiecesCollector -> {
				List<PoolStructurePiece> list = Lists.<PoolStructurePiece>newArrayList();
				list.add(poolStructurePiece);
				if (i > 0) {
					Box box = new Box((double)(k - j), (double)(o - j), (double)(l - j), (double)(k + j + 1), (double)(o + j + 1), (double)(l + j + 1));
					VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
					generate(context.noiseConfig(), i, bl, chunkGenerator, structureManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
					list.forEach(structurePiecesCollector::addPiece);
				}
			}));
		}
	}

	private static Optional<BlockPos> method_43566(
		StructurePoolElement structurePoolElement,
		Identifier identifier,
		BlockPos blockPos,
		BlockRotation blockRotation,
		StructureManager structureManager,
		ChunkRandom chunkRandom
	) {
		List<Structure.StructureBlockInfo> list = structurePoolElement.getStructureBlockInfos(structureManager, blockPos, blockRotation, chunkRandom);
		Optional<BlockPos> optional = Optional.empty();

		for (Structure.StructureBlockInfo structureBlockInfo : list) {
			Identifier identifier2 = Identifier.tryParse(structureBlockInfo.nbt.getString("name"));
			if (identifier.equals(identifier2)) {
				optional = Optional.of(structureBlockInfo.pos);
				break;
			}
		}

		return optional;
	}

	private static void generate(
		NoiseConfig noiseConfig,
		int i,
		boolean bl,
		ChunkGenerator chunkGenerator,
		StructureManager structureManager,
		HeightLimitView heightLimitView,
		AbstractRandom abstractRandom,
		Registry<StructurePool> registry,
		PoolStructurePiece poolStructurePiece,
		List<PoolStructurePiece> list,
		VoxelShape voxelShape
	) {
		StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(
			registry, i, chunkGenerator, structureManager, list, abstractRandom
		);
		structurePoolGenerator.structurePieces
			.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, new MutableObject<>(voxelShape), 0));

		while (!structurePoolGenerator.structurePieces.isEmpty()) {
			StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)structurePoolGenerator.structurePieces
				.removeFirst();
			structurePoolGenerator.generatePiece(
				shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, bl, heightLimitView, noiseConfig
			);
		}
	}

	static final class ShapedPoolStructurePiece {
		final PoolStructurePiece piece;
		final MutableObject<VoxelShape> pieceShape;
		final int currentSize;

		ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
			this.piece = piece;
			this.pieceShape = pieceShape;
			this.currentSize = currentSize;
		}
	}

	static final class StructurePoolGenerator {
		private final Registry<StructurePool> registry;
		private final int maxSize;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final List<? super PoolStructurePiece> children;
		private final AbstractRandom random;
		final Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = Queues.<StructurePoolBasedGenerator.ShapedPoolStructurePiece>newArrayDeque();

		StructurePoolGenerator(
			Registry<StructurePool> registry,
			int maxSize,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			List<? super PoolStructurePiece> list,
			AbstractRandom abstractRandom
		) {
			this.registry = registry;
			this.maxSize = maxSize;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.children = list;
			this.random = abstractRandom;
		}

		void generatePiece(
			PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig
		) {
			StructurePoolElement structurePoolElement = piece.getPoolElement();
			BlockPos blockPos = piece.getPos();
			BlockRotation blockRotation = piece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl = projection == StructurePool.Projection.RIGID;
			MutableObject<VoxelShape> mutableObject = new MutableObject<>();
			BlockBox blockBox = piece.getBoundingBox();
			int i = blockBox.getMinY();

			label137:
			for (Structure.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(
				this.structureManager, blockPos, blockRotation, this.random
			)) {
				Direction direction = JigsawBlock.getFacing(structureBlockInfo.state);
				BlockPos blockPos2 = structureBlockInfo.pos;
				BlockPos blockPos3 = blockPos2.offset(direction);
				int j = blockPos2.getY() - i;
				int k = -1;
				Identifier identifier = new Identifier(structureBlockInfo.nbt.getString("pool"));
				Optional<StructurePool> optional = this.registry.getOrEmpty(identifier);
				if (optional.isPresent() && (((StructurePool)optional.get()).getElementCount() != 0 || Objects.equals(identifier, StructurePools.EMPTY.getValue()))) {
					Identifier identifier2 = ((StructurePool)optional.get()).getTerminatorsId();
					Optional<StructurePool> optional2 = this.registry.getOrEmpty(identifier2);
					if (optional2.isPresent() && (((StructurePool)optional2.get()).getElementCount() != 0 || Objects.equals(identifier2, StructurePools.EMPTY.getValue()))) {
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
							list.addAll(((StructurePool)optional.get()).getElementIndicesInRandomOrder(this.random));
						}

						list.addAll(((StructurePool)optional2.get()).getElementIndicesInRandomOrder(this.random));

						for (StructurePoolElement structurePoolElement2 : list) {
							if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
								break;
							}

							for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
								List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(
									this.structureManager, BlockPos.ORIGIN, blockRotation2, this.random
								);
								BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureManager, BlockPos.ORIGIN, blockRotation2);
								int l;
								if (modifyBoundingBox && blockBox2.getBlockCountY() <= 16) {
									l = list2.stream().mapToInt(structureBlockInfox -> {
										if (!blockBox2.contains(structureBlockInfox.pos.offset(JigsawBlock.getFacing(structureBlockInfox.state)))) {
											return 0;
										} else {
											Identifier identifierx = new Identifier(structureBlockInfox.nbt.getString("pool"));
											Optional<StructurePool> optionalx = this.registry.getOrEmpty(identifierx);
											Optional<StructurePool> optional2x = optionalx.flatMap(pool -> this.registry.getOrEmpty(pool.getTerminatorsId()));
											int ix = (Integer)optionalx.map(pool -> pool.getHighestY(this.structureManager)).orElse(0);
											int jx = (Integer)optional2x.map(pool -> pool.getHighestY(this.structureManager)).orElse(0);
											return Math.max(ix, jx);
										}
									}).max().orElse(0);
								} else {
									l = 0;
								}

								for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
									if (JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) {
										BlockPos blockPos4 = structureBlockInfo2.pos;
										BlockPos blockPos5 = blockPos3.subtract(blockPos4);
										BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
										int m = blockBox3.getMinY();
										StructurePool.Projection projection2 = structurePoolElement2.getProjection();
										boolean bl3 = projection2 == StructurePool.Projection.RIGID;
										int n = blockPos4.getY();
										int o = j - n + JigsawBlock.getFacing(structureBlockInfo.state).getOffsetY();
										int p;
										if (bl && bl3) {
											p = i + o;
										} else {
											if (k == -1) {
												k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
											}

											p = k - n;
										}

										int q = p - m;
										BlockBox blockBox4 = blockBox3.offset(0, q, 0);
										BlockPos blockPos6 = blockPos5.add(0, q, 0);
										if (l > 0) {
											int r = Math.max(l + 1, blockBox4.getMaxY() - blockBox4.getMinY());
											blockBox4.encompass(new BlockPos(blockBox4.getMinX(), blockBox4.getMinY() + r, blockBox4.getMinZ()));
										}

										if (!VoxelShapes.matchesAnywhere(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) {
											mutableObject2.setValue(VoxelShapes.combine(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
											int r = piece.getGroundLevelDelta();
											int s;
											if (bl3) {
												s = r - o;
											} else {
												s = structurePoolElement2.getGroundLevelDelta();
											}

											PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureManager, structurePoolElement2, blockPos6, s, blockRotation2, blockBox4);
											int t;
											if (bl) {
												t = i + j;
											} else if (bl3) {
												t = p + n;
											} else {
												if (k == -1) {
													k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
												}

												t = k + o / 2;
											}

											piece.addJunction(new JigsawJunction(blockPos3.getX(), t - j + r, blockPos3.getZ(), o, projection2));
											poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), t - n + s, blockPos2.getZ(), -o, projection));
											this.children.add(poolStructurePiece);
											if (minY + 1 <= this.maxSize) {
												this.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, minY + 1));
											}
											continue label137;
										}
									}
								}
							}
						}
					} else {
						StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent fallback pool: {}", identifier2);
					}
				} else {
					StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent pool: {}", identifier);
				}
			}
		}
	}
}

package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
	static final Logger LOGGER = LogManager.getLogger();

	public static void method_30419(
		DynamicRegistryManager dynamicRegistryManager,
		StructurePoolFeatureConfig structurePoolFeatureConfig,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		ChunkGenerator chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		StructurePiecesHolder structurePiecesHolder,
		Random random,
		boolean bl,
		boolean bl2,
		HeightLimitView heightLimitView
	) {
		StructureFeature.init();
		List<PoolStructurePiece> list = Lists.<PoolStructurePiece>newArrayList();
		Registry<StructurePool> registry = dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY);
		BlockRotation blockRotation = BlockRotation.random(random);
		StructurePool structurePool = (StructurePool)structurePoolFeatureConfig.getStartPool().get();
		StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
		if (structurePoolElement != EmptyPoolElement.INSTANCE) {
			PoolStructurePiece poolStructurePiece = pieceFactory.create(
				structureManager,
				structurePoolElement,
				blockPos,
				structurePoolElement.getGroundLevelDelta(),
				blockRotation,
				structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation)
			);
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
			int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
			int k;
			if (bl2) {
				k = blockPos.getY() + chunkGenerator.getHeightOnGround(i, j, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
			} else {
				k = blockPos.getY();
			}

			int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
			poolStructurePiece.translate(0, k - l, 0);
			list.add(poolStructurePiece);
			if (structurePoolFeatureConfig.getSize() > 0) {
				int m = 80;
				Box box = new Box((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
				StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(
					registry, structurePoolFeatureConfig.getSize(), pieceFactory, chunkGenerator, structureManager, list, random
				);
				structurePoolGenerator.structurePieces
					.addLast(
						new StructurePoolBasedGenerator.ShapedPoolStructurePiece(
							poolStructurePiece,
							new MutableObject<>(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST)),
							k + 80,
							0
						)
					);

				while (!structurePoolGenerator.structurePieces.isEmpty()) {
					StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)structurePoolGenerator.structurePieces
						.removeFirst();
					structurePoolGenerator.generatePiece(
						shapedPoolStructurePiece.piece,
						shapedPoolStructurePiece.pieceShape,
						shapedPoolStructurePiece.minY,
						shapedPoolStructurePiece.currentSize,
						bl,
						heightLimitView
					);
				}

				list.forEach(structurePiecesHolder::addPiece);
			}
		}
	}

	public static void method_27230(
		DynamicRegistryManager dynamicRegistryManager,
		PoolStructurePiece poolStructurePiece,
		int i,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		ChunkGenerator chunkGenerator,
		StructureManager structureManager,
		List<? super PoolStructurePiece> list,
		Random random,
		HeightLimitView heightLimitView
	) {
		Registry<StructurePool> registry = dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY);
		StructurePoolBasedGenerator.StructurePoolGenerator structurePoolGenerator = new StructurePoolBasedGenerator.StructurePoolGenerator(
			registry, i, pieceFactory, chunkGenerator, structureManager, list, random
		);
		structurePoolGenerator.structurePieces
			.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, new MutableObject<>(VoxelShapes.UNBOUNDED), 0, 0));

		while (!structurePoolGenerator.structurePieces.isEmpty()) {
			StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)structurePoolGenerator.structurePieces
				.removeFirst();
			structurePoolGenerator.generatePiece(
				shapedPoolStructurePiece.piece,
				shapedPoolStructurePiece.pieceShape,
				shapedPoolStructurePiece.minY,
				shapedPoolStructurePiece.currentSize,
				false,
				heightLimitView
			);
		}
	}

	public interface PieceFactory {
		PoolStructurePiece create(
			StructureManager structureManager, StructurePoolElement poolElement, BlockPos pos, int i, BlockRotation rotation, BlockBox elementBounds
		);
	}

	static final class ShapedPoolStructurePiece {
		final PoolStructurePiece piece;
		final MutableObject<VoxelShape> pieceShape;
		final int minY;
		final int currentSize;

		ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, int currentSize) {
			this.piece = piece;
			this.pieceShape = pieceShape;
			this.minY = minY;
			this.currentSize = currentSize;
		}
	}

	static final class StructurePoolGenerator {
		private final Registry<StructurePool> registry;
		private final int maxSize;
		private final StructurePoolBasedGenerator.PieceFactory pieceFactory;
		private final ChunkGenerator chunkGenerator;
		private final StructureManager structureManager;
		private final List<? super PoolStructurePiece> children;
		private final Random random;
		final Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = Queues.<StructurePoolBasedGenerator.ShapedPoolStructurePiece>newArrayDeque();

		StructurePoolGenerator(
			Registry<StructurePool> registry,
			int maxSize,
			StructurePoolBasedGenerator.PieceFactory pieceFactory,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			List<? super PoolStructurePiece> children,
			Random random
		) {
			this.registry = registry;
			this.maxSize = maxSize;
			this.pieceFactory = pieceFactory;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.children = children;
			this.random = random;
		}

		void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, int currentSize, boolean bl, HeightLimitView world) {
			StructurePoolElement structurePoolElement = piece.getPoolElement();
			BlockPos blockPos = piece.getPos();
			BlockRotation blockRotation = piece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl2 = projection == StructurePool.Projection.RIGID;
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
						boolean bl3 = blockBox.contains(blockPos3);
						MutableObject<VoxelShape> mutableObject2;
						int l;
						if (bl3) {
							mutableObject2 = mutableObject;
							l = i;
							if (mutableObject.getValue() == null) {
								mutableObject.setValue(VoxelShapes.cuboid(Box.from(blockBox)));
							}
						} else {
							mutableObject2 = pieceShape;
							l = minY;
						}

						List<StructurePoolElement> list = Lists.<StructurePoolElement>newArrayList();
						if (currentSize != this.maxSize) {
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
								int m;
								if (bl && blockBox2.getBlockCountY() <= 16) {
									m = list2.stream().mapToInt(structureBlockInfox -> {
										if (!blockBox2.contains(structureBlockInfox.pos.offset(JigsawBlock.getFacing(structureBlockInfox.state)))) {
											return 0;
										} else {
											Identifier identifierx = new Identifier(structureBlockInfox.nbt.getString("pool"));
											Optional<StructurePool> optionalx = this.registry.getOrEmpty(identifierx);
											Optional<StructurePool> optional2x = optionalx.flatMap(structurePool -> this.registry.getOrEmpty(structurePool.getTerminatorsId()));
											int ix = (Integer)optionalx.map(structurePool -> structurePool.getHighestY(this.structureManager)).orElse(0);
											int jx = (Integer)optional2x.map(structurePool -> structurePool.getHighestY(this.structureManager)).orElse(0);
											return Math.max(ix, jx);
										}
									}).max().orElse(0);
								} else {
									m = 0;
								}

								for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
									if (JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) {
										BlockPos blockPos4 = structureBlockInfo2.pos;
										BlockPos blockPos5 = blockPos3.subtract(blockPos4);
										BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
										int n = blockBox3.getMinY();
										StructurePool.Projection projection2 = structurePoolElement2.getProjection();
										boolean bl4 = projection2 == StructurePool.Projection.RIGID;
										int o = blockPos4.getY();
										int p = j - o + JigsawBlock.getFacing(structureBlockInfo.state).getOffsetY();
										int q;
										if (bl2 && bl4) {
											q = i + p;
										} else {
											if (k == -1) {
												k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world);
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
											if (bl4) {
												t = s - p;
											} else {
												t = structurePoolElement2.getGroundLevelDelta();
											}

											PoolStructurePiece poolStructurePiece = this.pieceFactory
												.create(this.structureManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4);
											int u;
											if (bl2) {
												u = i + j;
											} else if (bl4) {
												u = q + o;
											} else {
												if (k == -1) {
													k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world);
												}

												u = k + p / 2;
											}

											piece.addJunction(new JigsawJunction(blockPos3.getX(), u - j + s, blockPos3.getZ(), p, projection2));
											poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), u - o + t, blockPos2.getZ(), -p, projection));
											this.children.add(poolStructurePiece);
											if (currentSize + 1 <= this.maxSize) {
												this.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, l, currentSize + 1));
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

package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructurePoolRegistry REGISTRY = new StructurePoolRegistry();

	public static void addPieces(
		Identifier startPoolId,
		int size,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos pos,
		List<StructurePiece> pieces,
		Random random
	) {
		StructureFeatures.initialize();
		new StructurePoolBasedGenerator.StructurePoolGenerator(startPoolId, size, pieceFactory, chunkGenerator, structureManager, pos, pieces, random);
	}

	static {
		REGISTRY.add(StructurePool.EMPTY);
	}

	public interface PieceFactory {
		PoolStructurePiece create(
			StructureManager structureManager, StructurePoolElement poolElement, BlockPos pos, int i, BlockRotation rotation, BlockBox elementBounds
		);
	}

	static final class ShapedPoolStructurePiece {
		private final PoolStructurePiece piece;
		private final AtomicReference<VoxelShape> pieceShape;
		private final int minY;
		private final int currentSize;

		private ShapedPoolStructurePiece(PoolStructurePiece piece, AtomicReference<VoxelShape> pieceShape, int minY, int currentSize) {
			this.piece = piece;
			this.pieceShape = pieceShape;
			this.minY = minY;
			this.currentSize = currentSize;
		}
	}

	static final class StructurePoolGenerator {
		private final int maxSize;
		private final StructurePoolBasedGenerator.PieceFactory pieceFactory;
		private final ChunkGenerator<?> chunkGenerator;
		private final StructureManager structureManager;
		private final List<StructurePiece> children;
		private final Random random;
		private final Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = Queues.<StructurePoolBasedGenerator.ShapedPoolStructurePiece>newArrayDeque();

		public StructurePoolGenerator(
			Identifier startingPool,
			int maxSize,
			StructurePoolBasedGenerator.PieceFactory pieceFactory,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			BlockPos blockPos,
			List<StructurePiece> children,
			Random random
		) {
			this.maxSize = maxSize;
			this.pieceFactory = pieceFactory;
			this.chunkGenerator = chunkGenerator;
			this.structureManager = structureManager;
			this.children = children;
			this.random = random;
			BlockRotation blockRotation = BlockRotation.random(random);
			StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(startingPool);
			StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
			PoolStructurePiece poolStructurePiece = pieceFactory.create(
				structureManager,
				structurePoolElement,
				blockPos,
				structurePoolElement.getGroundLevelDelta(),
				blockRotation,
				structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation)
			);
			BlockBox blockBox = poolStructurePiece.getBoundingBox();
			int i = (blockBox.maxX + blockBox.minX) / 2;
			int j = (blockBox.maxZ + blockBox.minZ) / 2;
			int k = chunkGenerator.getHeightOnGround(i, j, Heightmap.Type.WORLD_SURFACE_WG);
			poolStructurePiece.translate(0, k - (blockBox.minY + poolStructurePiece.getGroundLevelDelta()), 0);
			children.add(poolStructurePiece);
			if (maxSize > 0) {
				int l = 80;
				Box box = new Box((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
				this.structurePieces
					.addLast(
						new StructurePoolBasedGenerator.ShapedPoolStructurePiece(
							poolStructurePiece,
							new AtomicReference(VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST)),
							k + 80,
							0
						)
					);

				while (!this.structurePieces.isEmpty()) {
					StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = (StructurePoolBasedGenerator.ShapedPoolStructurePiece)this.structurePieces
						.removeFirst();
					this.generatePiece(
						shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.minY, shapedPoolStructurePiece.currentSize
					);
				}
			}
		}

		private void generatePiece(PoolStructurePiece piece, AtomicReference<VoxelShape> pieceShape, int minY, int currentSize) {
			StructurePoolElement structurePoolElement = piece.getPoolElement();
			BlockPos blockPos = piece.getPos();
			BlockRotation blockRotation = piece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl = projection == StructurePool.Projection.RIGID;
			AtomicReference<VoxelShape> atomicReference = new AtomicReference();
			BlockBox blockBox = piece.getBoundingBox();
			int i = blockBox.minY;

			label121:
			for (Structure.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(
				this.structureManager, blockPos, blockRotation, this.random
			)) {
				Direction direction = JigsawBlock.method_26378(structureBlockInfo.state);
				BlockPos blockPos2 = structureBlockInfo.pos;
				BlockPos blockPos3 = blockPos2.offset(direction);
				int j = blockPos2.getY() - i;
				int k = -1;
				StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(structureBlockInfo.tag.getString("pool")));
				StructurePool structurePool2 = StructurePoolBasedGenerator.REGISTRY.get(structurePool.getTerminatorsId());
				if (structurePool != StructurePool.INVALID && (structurePool.getElementCount() != 0 || structurePool == StructurePool.EMPTY)) {
					boolean bl2 = blockBox.contains(blockPos3);
					AtomicReference<VoxelShape> atomicReference2;
					int l;
					if (bl2) {
						atomicReference2 = atomicReference;
						l = i;
						if (atomicReference.get() == null) {
							atomicReference.set(VoxelShapes.cuboid(Box.from(blockBox)));
						}
					} else {
						atomicReference2 = pieceShape;
						l = minY;
					}

					List<StructurePoolElement> list = Lists.<StructurePoolElement>newArrayList();
					if (currentSize != this.maxSize) {
						list.addAll(structurePool.getElementIndicesInRandomOrder(this.random));
					}

					list.addAll(structurePool2.getElementIndicesInRandomOrder(this.random));

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
							if (blockBox2.getBlockCountY() > 16) {
								m = 0;
							} else {
								m = list2.stream().mapToInt(structureBlockInfox -> {
									if (!blockBox2.contains(structureBlockInfox.pos.offset(JigsawBlock.method_26378(structureBlockInfox.state)))) {
										return 0;
									} else {
										Identifier identifier = new Identifier(structureBlockInfox.tag.getString("pool"));
										StructurePool structurePoolx = StructurePoolBasedGenerator.REGISTRY.get(identifier);
										StructurePool structurePool2x = StructurePoolBasedGenerator.REGISTRY.get(structurePoolx.getTerminatorsId());
										return Math.max(structurePoolx.getHighestY(this.structureManager), structurePool2x.getHighestY(this.structureManager));
									}
								}).max().orElse(0);
							}

							for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
								if (JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) {
									BlockPos blockPos4 = structureBlockInfo2.pos;
									BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
									BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
									int n = blockBox3.minY;
									StructurePool.Projection projection2 = structurePoolElement2.getProjection();
									boolean bl3 = projection2 == StructurePool.Projection.RIGID;
									int o = blockPos4.getY();
									int p = j - o + JigsawBlock.method_26378(structureBlockInfo.state).getOffsetY();
									int q;
									if (bl && bl3) {
										q = i + p;
									} else {
										if (k == -1) {
											k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
										}

										q = k - o;
									}

									int r = q - n;
									BlockBox blockBox4 = blockBox3.translated(0, r, 0);
									BlockPos blockPos6 = blockPos5.add(0, r, 0);
									if (m > 0) {
										int s = Math.max(m + 1, blockBox4.maxY - blockBox4.minY);
										blockBox4.maxY = blockBox4.minY + s;
									}

									if (!VoxelShapes.matchesAnywhere(
										(VoxelShape)atomicReference2.get(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND
									)) {
										atomicReference2.set(VoxelShapes.combine((VoxelShape)atomicReference2.get(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
										int s = piece.getGroundLevelDelta();
										int t;
										if (bl3) {
											t = s - p;
										} else {
											t = structurePoolElement2.getGroundLevelDelta();
										}

										PoolStructurePiece poolStructurePiece = this.pieceFactory
											.create(this.structureManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4);
										int u;
										if (bl) {
											u = i + j;
										} else if (bl3) {
											u = q + o;
										} else {
											if (k == -1) {
												k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
											}

											u = k + p / 2;
										}

										piece.addJunction(new JigsawJunction(blockPos3.getX(), u - j + s, blockPos3.getZ(), p, projection2));
										poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), u - o + t, blockPos2.getZ(), -p, projection));
										this.children.add(poolStructurePiece);
										if (currentSize + 1 <= this.maxSize) {
											this.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, atomicReference2, l, currentSize + 1));
										}
										continue label121;
									}
								}
							}
						}
					}
				} else {
					StructurePoolBasedGenerator.LOGGER.warn("Empty or none existent pool: {}", structureBlockInfo.tag.getString("pool"));
				}
			}
		}
	}
}

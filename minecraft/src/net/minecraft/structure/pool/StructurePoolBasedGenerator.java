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
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeatures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructurePoolBasedGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructurePoolRegistry REGISTRY = new StructurePoolRegistry();

	public static void addPieces(
		Identifier identifier,
		int i,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<StructurePiece> list,
		Random random
	) {
		StructureFeatures.initialize();
		new StructurePoolBasedGenerator.class_4182(identifier, i, pieceFactory, chunkGenerator, structureManager, blockPos, list, random);
	}

	static {
		REGISTRY.add(StructurePool.EMPTY);
	}

	public interface PieceFactory {
		PoolStructurePiece create(
			StructureManager structureManager,
			StructurePoolElement structurePoolElement,
			BlockPos blockPos,
			int i,
			Rotation rotation,
			MutableIntBoundingBox mutableIntBoundingBox
		);
	}

	static final class class_4181 {
		private final PoolStructurePiece field_18696;
		private final AtomicReference<VoxelShape> field_18697;
		private final int field_18698;
		private final int field_18699;

		private class_4181(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
			this.field_18696 = poolStructurePiece;
			this.field_18697 = atomicReference;
			this.field_18698 = i;
			this.field_18699 = j;
		}
	}

	static final class class_4182 {
		private final int field_18700;
		private final StructurePoolBasedGenerator.PieceFactory field_18701;
		private final ChunkGenerator<?> field_18702;
		private final StructureManager field_18703;
		private final List<StructurePiece> field_18704;
		private final Random field_18705;
		private final Deque<StructurePoolBasedGenerator.class_4181> field_18706 = Queues.<StructurePoolBasedGenerator.class_4181>newArrayDeque();

		public class_4182(
			Identifier identifier,
			int i,
			StructurePoolBasedGenerator.PieceFactory pieceFactory,
			ChunkGenerator<?> chunkGenerator,
			StructureManager structureManager,
			BlockPos blockPos,
			List<StructurePiece> list,
			Random random
		) {
			this.field_18700 = i;
			this.field_18701 = pieceFactory;
			this.field_18702 = chunkGenerator;
			this.field_18703 = structureManager;
			this.field_18704 = list;
			this.field_18705 = random;
			Rotation rotation = Rotation.random(random);
			StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(identifier);
			StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
			PoolStructurePiece poolStructurePiece = pieceFactory.create(
				structureManager,
				structurePoolElement,
				blockPos,
				structurePoolElement.method_19308(),
				rotation,
				structurePoolElement.getBoundingBox(structureManager, blockPos, rotation)
			);
			MutableIntBoundingBox mutableIntBoundingBox = poolStructurePiece.getBoundingBox();
			int j = (mutableIntBoundingBox.maxX + mutableIntBoundingBox.minX) / 2;
			int k = (mutableIntBoundingBox.maxZ + mutableIntBoundingBox.minZ) / 2;
			int l = chunkGenerator.getHeightInGround(j, k, Heightmap.Type.WORLD_SURFACE_WG);
			poolStructurePiece.translate(0, l - (mutableIntBoundingBox.minY + poolStructurePiece.getGroundLevelDelta()), 0);
			list.add(poolStructurePiece);
			if (i > 0) {
				int m = 80;
				BoundingBox boundingBox = new BoundingBox(
					(double)(j - 80), (double)(l - 80), (double)(k - 80), (double)(j + 80 + 1), (double)(l + 80 + 1), (double)(k + 80 + 1)
				);
				this.field_18706
					.addLast(
						new StructurePoolBasedGenerator.class_4181(
							poolStructurePiece,
							new AtomicReference(
								VoxelShapes.combineAndSimplify(
									VoxelShapes.cuboid(boundingBox), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox)), BooleanBiFunction.ONLY_FIRST
								)
							),
							l + 80,
							0
						)
					);

				while (!this.field_18706.isEmpty()) {
					StructurePoolBasedGenerator.class_4181 lv = (StructurePoolBasedGenerator.class_4181)this.field_18706.removeFirst();
					this.method_19306(lv.field_18696, lv.field_18697, lv.field_18698, lv.field_18699);
				}
			}
		}

		private void method_19306(PoolStructurePiece poolStructurePiece, AtomicReference<VoxelShape> atomicReference, int i, int j) {
			StructurePoolElement structurePoolElement = poolStructurePiece.getPoolElement();
			BlockPos blockPos = poolStructurePiece.getPos();
			Rotation rotation = poolStructurePiece.getRotation();
			StructurePool.Projection projection = structurePoolElement.getProjection();
			boolean bl = projection == StructurePool.Projection.RIGID;
			AtomicReference<VoxelShape> atomicReference2 = new AtomicReference();
			MutableIntBoundingBox mutableIntBoundingBox = poolStructurePiece.getBoundingBox();
			int k = mutableIntBoundingBox.minY;

			label121:
			for (Structure.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(this.field_18703, blockPos, rotation, this.field_18705)) {
				Direction direction = structureBlockInfo.state.get(JigsawBlock.FACING);
				BlockPos blockPos2 = structureBlockInfo.pos;
				BlockPos blockPos3 = blockPos2.offset(direction);
				int l = blockPos2.getY() - k;
				int m = -1;
				StructurePool structurePool = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(structureBlockInfo.tag.getString("target_pool")));
				StructurePool structurePool2 = StructurePoolBasedGenerator.REGISTRY.get(structurePool.getTerminatorsId());
				if (structurePool != StructurePool.INVALID && (structurePool.getElementCount() != 0 || structurePool == StructurePool.EMPTY)) {
					boolean bl2 = mutableIntBoundingBox.contains(blockPos3);
					AtomicReference<VoxelShape> atomicReference3;
					int n;
					if (bl2) {
						atomicReference3 = atomicReference2;
						n = k;
						if (atomicReference2.get() == null) {
							atomicReference2.set(VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox)));
						}
					} else {
						atomicReference3 = atomicReference;
						n = i;
					}

					List<StructurePoolElement> list = Lists.<StructurePoolElement>newArrayList();
					if (j != this.field_18700) {
						list.addAll(structurePool.getElementIndicesInRandomOrder(this.field_18705));
					}

					list.addAll(structurePool2.getElementIndicesInRandomOrder(this.field_18705));

					for (StructurePoolElement structurePoolElement2 : list) {
						if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
							break;
						}

						for (Rotation rotation2 : Rotation.method_16547(this.field_18705)) {
							List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.field_18703, BlockPos.ORIGIN, rotation2, this.field_18705);
							MutableIntBoundingBox mutableIntBoundingBox2 = structurePoolElement2.getBoundingBox(this.field_18703, BlockPos.ORIGIN, rotation2);
							int o;
							if (mutableIntBoundingBox2.getBlockCountY() > 16) {
								o = 0;
							} else {
								o = list2.stream().mapToInt(structureBlockInfox -> {
									if (!mutableIntBoundingBox2.contains(structureBlockInfox.pos.offset(structureBlockInfox.state.get(JigsawBlock.FACING)))) {
										return 0;
									} else {
										Identifier identifier = new Identifier(structureBlockInfox.tag.getString("target_pool"));
										StructurePool structurePoolx = StructurePoolBasedGenerator.REGISTRY.get(identifier);
										StructurePool structurePool2x = StructurePoolBasedGenerator.REGISTRY.get(structurePoolx.getTerminatorsId());
										return Math.max(structurePoolx.method_19309(this.field_18703), structurePool2x.method_19309(this.field_18703));
									}
								}).max().orElse(0);
							}

							for (Structure.StructureBlockInfo structureBlockInfo2 : list2) {
								if (JigsawBlock.method_16546(structureBlockInfo, structureBlockInfo2)) {
									BlockPos blockPos4 = structureBlockInfo2.pos;
									BlockPos blockPos5 = new BlockPos(blockPos3.getX() - blockPos4.getX(), blockPos3.getY() - blockPos4.getY(), blockPos3.getZ() - blockPos4.getZ());
									MutableIntBoundingBox mutableIntBoundingBox3 = structurePoolElement2.getBoundingBox(this.field_18703, blockPos5, rotation2);
									int p = mutableIntBoundingBox3.minY;
									StructurePool.Projection projection2 = structurePoolElement2.getProjection();
									boolean bl3 = projection2 == StructurePool.Projection.RIGID;
									int q = blockPos4.getY();
									int r = l - q + ((Direction)structureBlockInfo.state.get(JigsawBlock.FACING)).getOffsetY();
									int s;
									if (bl && bl3) {
										s = k + r;
									} else {
										if (m == -1) {
											m = this.field_18702.getHeightInGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
										}

										s = m + r;
									}

									int t = s - p;
									MutableIntBoundingBox mutableIntBoundingBox4 = mutableIntBoundingBox3.method_19311(0, t, 0);
									BlockPos blockPos6 = blockPos5.add(0, t, 0);
									if (o > 0) {
										int u = Math.max(o + 1, mutableIntBoundingBox4.maxY - mutableIntBoundingBox4.minY);
										mutableIntBoundingBox4.maxY = mutableIntBoundingBox4.minY + u;
									}

									if (!VoxelShapes.matchesAnywhere(
										(VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND
									)) {
										atomicReference3.set(
											VoxelShapes.combine((VoxelShape)atomicReference3.get(), VoxelShapes.cuboid(BoundingBox.from(mutableIntBoundingBox4)), BooleanBiFunction.ONLY_FIRST)
										);
										int u = poolStructurePiece.getGroundLevelDelta();
										int v;
										if (bl3) {
											v = u - r;
										} else {
											v = structurePoolElement2.method_19308();
										}

										PoolStructurePiece poolStructurePiece2 = this.field_18701
											.create(this.field_18703, structurePoolElement2, blockPos6, v, rotation2, mutableIntBoundingBox4);
										int w;
										if (bl) {
											w = k + l;
										} else if (bl3) {
											w = s + q;
										} else {
											if (m == -1) {
												m = this.field_18702.getHeightInGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
											}

											w = m + r / 2;
										}

										poolStructurePiece.addJunction(new JigsawJunction(blockPos3.getX(), w - l + u, blockPos3.getZ(), r, projection2));
										poolStructurePiece2.addJunction(new JigsawJunction(blockPos2.getX(), w - q + v, blockPos2.getZ(), -r, projection));
										this.field_18704.add(poolStructurePiece2);
										if (j + 1 <= this.field_18700) {
											this.field_18706.addLast(new StructurePoolBasedGenerator.class_4181(poolStructurePiece2, atomicReference3, n, j + 1));
										}
										continue label121;
									}
								}
							}
						}
					}
				} else {
					StructurePoolBasedGenerator.LOGGER.warn("Empty or none existent pool: {}", structureBlockInfo.tag.getString("target_pool"));
				}
			}
		}
	}
}

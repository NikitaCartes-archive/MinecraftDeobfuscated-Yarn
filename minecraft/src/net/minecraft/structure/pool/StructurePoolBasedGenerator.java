package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
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
		Rotation rotation = Rotation.random(random);
		StructurePool structurePool = REGISTRY.get(identifier);
		StructurePoolElement structurePoolElement = structurePool.getRandomElement(random);
		PoolStructurePiece poolStructurePiece = pieceFactory.create(structureManager, structurePoolElement, blockPos, 1, rotation);
		MutableIntBoundingBox mutableIntBoundingBox = poolStructurePiece.getBoundingBox();
		int j = (mutableIntBoundingBox.maxX + mutableIntBoundingBox.minX) / 2;
		int k = (mutableIntBoundingBox.maxZ + mutableIntBoundingBox.minZ) / 2;
		poolStructurePiece.translate(0, chunkGenerator.getHeightOnGround(j, k, Heightmap.Type.WORLD_SURFACE_WG) - mutableIntBoundingBox.minY, 0);
		addPieces(pieceFactory, poolStructurePiece, chunkGenerator, structureManager, list, random, 0, i);
	}

	private static void addPieces(
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		PoolStructurePiece poolStructurePiece,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		List<StructurePiece> list,
		Random random,
		int i,
		int j
	) {
		list.add(poolStructurePiece);
		if (i <= j) {
			StructurePoolElement structurePoolElement = poolStructurePiece.getPoolElement();
			BlockPos blockPos = poolStructurePiece.getPos();
			Rotation rotation = poolStructurePiece.getRotation();
			List<MutableIntBoundingBox> list2 = Lists.<MutableIntBoundingBox>newArrayList();

			for (Structure.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(structureManager, blockPos, rotation, random)) {
				Direction direction = structureBlockInfo.state.get(JigsawBlock.FACING);
				BlockPos blockPos2 = structureBlockInfo.pos.offset(direction);
				StructurePool structurePool = REGISTRY.get(new Identifier(structureBlockInfo.tag.getString("target_pool")));
				StructurePool structurePool2 = REGISTRY.get(structurePool.getTerminatorsId());
				if (structurePool != StructurePool.INVALID && (structurePool.getElementCount() != 0 || structurePool == StructurePool.EMPTY)) {
					MutableIntBoundingBox mutableIntBoundingBox = structurePoolElement.getBoundingBox(structureManager, blockPos, rotation);
					if (i == j
						|| !addPieces(
							pieceFactory,
							poolStructurePiece,
							chunkGenerator,
							structureManager,
							list,
							random,
							structurePoolElement,
							structureBlockInfo,
							mutableIntBoundingBox,
							list2,
							blockPos2,
							structurePool,
							i,
							j
						)) {
						addPieces(
							pieceFactory,
							poolStructurePiece,
							chunkGenerator,
							structureManager,
							list,
							random,
							structurePoolElement,
							structureBlockInfo,
							mutableIntBoundingBox,
							list2,
							blockPos2,
							structurePool2,
							i,
							j
						);
					}
				} else {
					LOGGER.warn("Empty or none existent pool: {}", structureBlockInfo.tag.getString("target_pool"));
				}
			}
		}
	}

	private static boolean addPieces(
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		PoolStructurePiece poolStructurePiece,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		List<StructurePiece> list,
		Random random,
		StructurePoolElement structurePoolElement,
		Structure.StructureBlockInfo structureBlockInfo,
		MutableIntBoundingBox mutableIntBoundingBox,
		List<MutableIntBoundingBox> list2,
		BlockPos blockPos,
		StructurePool structurePool,
		int i,
		int j
	) {
		boolean bl = mutableIntBoundingBox.contains(blockPos);

		for (int k : structurePool.getElementIndicesInRandomOrder(random)) {
			StructurePoolElement structurePoolElement2 = structurePool.getElement(k);
			if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
				return true;
			}

			for (Rotation rotation : Rotation.method_16547(random)) {
				for (Structure.StructureBlockInfo structureBlockInfo2 : structurePoolElement2.getStructureBlockInfos(structureManager, BlockPos.ORIGIN, rotation, random)) {
					if (JigsawBlock.method_16546(structureBlockInfo, structureBlockInfo2)) {
						BlockPos blockPos2 = new BlockPos(
							blockPos.getX() - structureBlockInfo2.pos.getX(), blockPos.getY() - structureBlockInfo2.pos.getY(), blockPos.getZ() - structureBlockInfo2.pos.getZ()
						);
						StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
						StructurePool.Projection projection2 = structurePoolElement2.getProjection();
						int l = structureBlockInfo.pos.getY() - mutableIntBoundingBox.minY;
						int m = structureBlockInfo2.pos.getY();
						int n = l - m + ((Direction)structureBlockInfo.state.get(JigsawBlock.FACING)).getOffsetY();
						int o = poolStructurePiece.getGroundLevelDelta();
						int p;
						if (projection2 == StructurePool.Projection.RIGID) {
							p = o - n;
						} else {
							p = 1;
						}

						MutableIntBoundingBox mutableIntBoundingBox2 = structurePoolElement2.getBoundingBox(structureManager, blockPos2, rotation);
						int q;
						if (projection == StructurePool.Projection.RIGID && projection2 == StructurePool.Projection.RIGID) {
							q = mutableIntBoundingBox.minY + n;
						} else {
							q = chunkGenerator.getHeightOnGround(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n;
						}

						int r = q - mutableIntBoundingBox2.minY;
						mutableIntBoundingBox2.translate(0, r, 0);
						if (mutableIntBoundingBox2.maxY - mutableIntBoundingBox2.minY < 16) {
							mutableIntBoundingBox2.maxY += 8;
							mutableIntBoundingBox2.minY -= 8;
						}

						blockPos2 = blockPos2.add(0, r, 0);
						if (bl && method_16608(mutableIntBoundingBox, list2, mutableIntBoundingBox2) || method_16604(list, mutableIntBoundingBox2)) {
							if (bl) {
								list2.add(mutableIntBoundingBox2);
							}

							PoolStructurePiece poolStructurePiece2 = pieceFactory.create(structureManager, structurePoolElement2, blockPos2, p, rotation);
							int s;
							if (projection == StructurePool.Projection.RIGID) {
								s = mutableIntBoundingBox.minY + l;
							} else if (projection2 == StructurePool.Projection.RIGID) {
								s = q + m;
							} else {
								s = chunkGenerator.getHeightOnGround(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n / 2;
							}

							poolStructurePiece.addJunction(new JigsawJunction(blockPos.getX(), s - l + o, blockPos.getZ(), n, projection2));
							poolStructurePiece2.addJunction(
								new JigsawJunction(structureBlockInfo.pos.getX(), s - m + p, structureBlockInfo.pos.getZ(), -n, structurePoolElement.getProjection())
							);
							addPieces(pieceFactory, poolStructurePiece2, chunkGenerator, structureManager, list, random, i + 1, j);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private static boolean method_16608(
		MutableIntBoundingBox mutableIntBoundingBox, List<MutableIntBoundingBox> list, MutableIntBoundingBox mutableIntBoundingBox2
	) {
		if (mutableIntBoundingBox2.minX >= mutableIntBoundingBox.minX
			&& mutableIntBoundingBox2.maxX <= mutableIntBoundingBox.maxX
			&& mutableIntBoundingBox2.minZ >= mutableIntBoundingBox.minZ
			&& mutableIntBoundingBox2.maxZ <= mutableIntBoundingBox.maxZ) {
			for (MutableIntBoundingBox mutableIntBoundingBox3 : list) {
				if (mutableIntBoundingBox2.intersects(mutableIntBoundingBox3)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private static boolean method_16604(List<StructurePiece> list, MutableIntBoundingBox mutableIntBoundingBox) {
		for (StructurePiece structurePiece : list) {
			if (mutableIntBoundingBox.intersects(structurePiece.getBoundingBox())) {
				return false;
			}
		}

		return true;
	}

	static {
		REGISTRY.add(StructurePool.EMPTY);
	}

	public interface PieceFactory {
		PoolStructurePiece create(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int i, Rotation rotation);
	}
}

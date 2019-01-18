package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeatures;
import net.minecraft.world.gen.feature.structure.JigsawJunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3778 {
	private static final Logger field_16665 = LogManager.getLogger();
	public static final class_3787 field_16666 = new class_3787();

	public static void method_16605(
		Identifier identifier,
		int i,
		class_3778.class_3779 arg,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<class_3443> list,
		Random random
	) {
		StructureFeatures.method_16651();
		Rotation rotation = Rotation.method_16548(random);
		class_3785 lv = field_16666.method_16639(identifier);
		class_3784 lv2 = lv.method_16631(random);
		class_3790 lv3 = arg.create(structureManager, lv2, blockPos, 1, rotation);
		MutableIntBoundingBox mutableIntBoundingBox = lv3.method_14935();
		int j = (mutableIntBoundingBox.maxX + mutableIntBoundingBox.minX) / 2;
		int k = (mutableIntBoundingBox.maxZ + mutableIntBoundingBox.minZ) / 2;
		lv3.translate(0, chunkGenerator.produceHeight(j, k, Heightmap.Type.WORLD_SURFACE_WG) - mutableIntBoundingBox.minY, 0);
		method_16607(arg, lv3, chunkGenerator, structureManager, list, random, 0, i);
	}

	private static void method_16607(
		class_3778.class_3779 arg,
		class_3790 arg2,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		List<class_3443> list,
		Random random,
		int i,
		int j
	) {
		list.add(arg2);
		if (i <= j) {
			class_3784 lv = arg2.method_16644();
			BlockPos blockPos = arg2.method_16648();
			Rotation rotation = arg2.method_16888();
			List<MutableIntBoundingBox> list2 = Lists.<MutableIntBoundingBox>newArrayList();

			for (Structure.StructureBlockInfo structureBlockInfo : lv.method_16627(structureManager, blockPos, rotation, random)) {
				Direction direction = structureBlockInfo.state.get(JigsawBlock.FACING);
				BlockPos blockPos2 = structureBlockInfo.pos.offset(direction);
				class_3785 lv2 = field_16666.method_16639(new Identifier(structureBlockInfo.tag.getString("target_pool")));
				class_3785 lv3 = field_16666.method_16639(lv2.method_16634());
				if (lv2 != class_3785.field_16746 && (lv2.method_16632() != 0 || lv2 == class_3785.field_16679)) {
					MutableIntBoundingBox mutableIntBoundingBox = lv.method_16628(structureManager, blockPos, rotation);
					if (i == j
						|| !method_16606(arg, arg2, chunkGenerator, structureManager, list, random, lv, structureBlockInfo, mutableIntBoundingBox, list2, blockPos2, lv2, i, j)) {
						method_16606(arg, arg2, chunkGenerator, structureManager, list, random, lv, structureBlockInfo, mutableIntBoundingBox, list2, blockPos2, lv3, i, j);
					}
				} else {
					field_16665.warn("Empty or none existent pool: {}", structureBlockInfo.tag.getString("target_pool"));
				}
			}
		}
	}

	private static boolean method_16606(
		class_3778.class_3779 arg,
		class_3790 arg2,
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		List<class_3443> list,
		Random random,
		class_3784 arg3,
		Structure.StructureBlockInfo structureBlockInfo,
		MutableIntBoundingBox mutableIntBoundingBox,
		List<MutableIntBoundingBox> list2,
		BlockPos blockPos,
		class_3785 arg4,
		int i,
		int j
	) {
		boolean bl = mutableIntBoundingBox.contains(blockPos);

		for (int k : arg4.method_16633(random)) {
			class_3784 lv = arg4.method_16630(k);
			if (lv == class_3777.field_16663) {
				return true;
			}

			for (Rotation rotation : Rotation.method_16547(random)) {
				for (Structure.StructureBlockInfo structureBlockInfo2 : lv.method_16627(structureManager, BlockPos.ORIGIN, rotation, random)) {
					if (JigsawBlock.method_16546(structureBlockInfo, structureBlockInfo2)) {
						BlockPos blockPos2 = new BlockPos(
							blockPos.getX() - structureBlockInfo2.pos.getX(), blockPos.getY() - structureBlockInfo2.pos.getY(), blockPos.getZ() - structureBlockInfo2.pos.getZ()
						);
						class_3785.Projection projection = arg2.method_16644().method_16624();
						class_3785.Projection projection2 = lv.method_16624();
						int l = structureBlockInfo.pos.getY() - mutableIntBoundingBox.minY;
						int m = structureBlockInfo2.pos.getY();
						int n = l - m + ((Direction)structureBlockInfo.state.get(JigsawBlock.FACING)).getOffsetY();
						int o = arg2.method_16646();
						int p;
						if (projection2 == class_3785.Projection.RIGID) {
							p = o - n;
						} else {
							p = 1;
						}

						MutableIntBoundingBox mutableIntBoundingBox2 = lv.method_16628(structureManager, blockPos2, rotation);
						int q;
						if (projection == class_3785.Projection.RIGID && projection2 == class_3785.Projection.RIGID) {
							q = mutableIntBoundingBox.minY + n;
						} else {
							q = chunkGenerator.produceHeight(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n;
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

							class_3790 lv2 = arg.create(structureManager, lv, blockPos2, p, rotation);
							int s;
							if (projection == class_3785.Projection.RIGID) {
								s = mutableIntBoundingBox.minY + l;
							} else if (projection2 == class_3785.Projection.RIGID) {
								s = q + m;
							} else {
								s = chunkGenerator.produceHeight(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n / 2;
							}

							arg2.method_16647(new JigsawJunction(blockPos.getX(), s - l + o, blockPos.getZ(), n, projection2));
							lv2.method_16647(new JigsawJunction(structureBlockInfo.pos.getX(), s - m + p, structureBlockInfo.pos.getZ(), -n, arg3.method_16624()));
							method_16607(arg, lv2, chunkGenerator, structureManager, list, random, i + 1, j);
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

	private static boolean method_16604(List<class_3443> list, MutableIntBoundingBox mutableIntBoundingBox) {
		for (class_3443 lv : list) {
			if (mutableIntBoundingBox.intersects(lv.method_14935())) {
				return false;
			}
		}

		return true;
	}

	static {
		field_16666.method_16640(class_3785.field_16679);
	}

	public interface class_3779 {
		class_3790 create(StructureManager structureManager, class_3784 arg, BlockPos blockPos, int i, Rotation rotation);
	}
}

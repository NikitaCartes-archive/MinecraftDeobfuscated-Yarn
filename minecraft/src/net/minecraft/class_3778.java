package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.JigsawBlock;
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
		class_3485 arg2,
		BlockPos blockPos,
		List<class_3443> list,
		Random random
	) {
		StructureFeatures.method_16651();
		Rotation rotation = Rotation.method_16548(random);
		class_3785 lv = field_16666.method_16639(identifier);
		class_3784 lv2 = lv.method_16631(random);
		class_3790 lv3 = arg.create(arg2, lv2, blockPos, 1, rotation);
		MutableIntBoundingBox mutableIntBoundingBox = lv3.method_14935();
		int j = (mutableIntBoundingBox.maxX + mutableIntBoundingBox.minX) / 2;
		int k = (mutableIntBoundingBox.maxZ + mutableIntBoundingBox.minZ) / 2;
		lv3.translate(0, chunkGenerator.produceHeight(j, k, Heightmap.Type.WORLD_SURFACE_WG) - mutableIntBoundingBox.minY, 0);
		method_16607(arg, lv3, chunkGenerator, arg2, list, random, 0, i);
	}

	private static void method_16607(
		class_3778.class_3779 arg, class_3790 arg2, ChunkGenerator<?> chunkGenerator, class_3485 arg3, List<class_3443> list, Random random, int i, int j
	) {
		list.add(arg2);
		if (i <= j) {
			class_3784 lv = arg2.method_16644();
			BlockPos blockPos = arg2.method_16648();
			Rotation rotation = arg2.method_16888();
			List<MutableIntBoundingBox> list2 = Lists.<MutableIntBoundingBox>newArrayList();

			for (class_3499.class_3501 lv2 : lv.method_16627(arg3, blockPos, rotation, random)) {
				Direction direction = lv2.field_15596.get(JigsawBlock.field_10927);
				BlockPos blockPos2 = lv2.field_15597.method_10093(direction);
				class_3785 lv3 = field_16666.method_16639(new Identifier(lv2.field_15595.getString("target_pool")));
				class_3785 lv4 = field_16666.method_16639(lv3.method_16634());
				if (lv3 != class_3785.field_16746 && (lv3.method_16632() != 0 || lv3 == class_3785.field_16679)) {
					MutableIntBoundingBox mutableIntBoundingBox = lv.method_16628(arg3, blockPos, rotation);
					if (i == j || !method_16606(arg, arg2, chunkGenerator, arg3, list, random, lv, lv2, mutableIntBoundingBox, list2, blockPos2, lv3, i, j)) {
						method_16606(arg, arg2, chunkGenerator, arg3, list, random, lv, lv2, mutableIntBoundingBox, list2, blockPos2, lv4, i, j);
					}
				} else {
					field_16665.warn("Empty or none existent pool: {}", lv2.field_15595.getString("target_pool"));
				}
			}
		}
	}

	private static boolean method_16606(
		class_3778.class_3779 arg,
		class_3790 arg2,
		ChunkGenerator<?> chunkGenerator,
		class_3485 arg3,
		List<class_3443> list,
		Random random,
		class_3784 arg4,
		class_3499.class_3501 arg5,
		MutableIntBoundingBox mutableIntBoundingBox,
		List<MutableIntBoundingBox> list2,
		BlockPos blockPos,
		class_3785 arg6,
		int i,
		int j
	) {
		boolean bl = mutableIntBoundingBox.contains(blockPos);

		for (int k : arg6.method_16633(random)) {
			class_3784 lv = arg6.method_16630(k);
			if (lv == class_3777.field_16663) {
				return true;
			}

			for (Rotation rotation : Rotation.method_16547(random)) {
				for (class_3499.class_3501 lv2 : lv.method_16627(arg3, new BlockPos(0, 0, 0), rotation, random)) {
					if (JigsawBlock.method_16546(arg5, lv2)) {
						BlockPos blockPos2 = new BlockPos(
							blockPos.getX() - lv2.field_15597.getX(), blockPos.getY() - lv2.field_15597.getY(), blockPos.getZ() - lv2.field_15597.getZ()
						);
						class_3785.Projection projection = arg2.method_16644().method_16624();
						class_3785.Projection projection2 = lv.method_16624();
						int l = arg5.field_15597.getY() - mutableIntBoundingBox.minY;
						int m = lv2.field_15597.getY();
						int n = l - m + ((Direction)arg5.field_15596.get(JigsawBlock.field_10927)).getOffsetY();
						int o = arg2.method_16646();
						int p;
						if (projection2 == class_3785.Projection.RIGID) {
							p = o - n;
						} else {
							p = 1;
						}

						MutableIntBoundingBox mutableIntBoundingBox2 = lv.method_16628(arg3, blockPos2, rotation);
						int q;
						if (projection == class_3785.Projection.RIGID && projection2 == class_3785.Projection.RIGID) {
							q = mutableIntBoundingBox.minY + n;
						} else {
							q = chunkGenerator.produceHeight(arg5.field_15597.getX(), arg5.field_15597.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n;
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

							class_3790 lv3 = arg.create(arg3, lv, blockPos2, p, rotation);
							int s;
							if (projection == class_3785.Projection.RIGID) {
								s = mutableIntBoundingBox.minY + l;
							} else if (projection2 == class_3785.Projection.RIGID) {
								s = q + m;
							} else {
								s = chunkGenerator.produceHeight(arg5.field_15597.getX(), arg5.field_15597.getZ(), Heightmap.Type.WORLD_SURFACE_WG) - 1 + n / 2;
							}

							arg2.method_16647(new JigsawJunction(blockPos.getX(), s - l + o, blockPos.getZ(), n, projection2));
							lv3.method_16647(new JigsawJunction(arg5.field_15597.getX(), s - m + p, arg5.field_15597.getZ(), -n, arg4.method_16624()));
							method_16607(arg, lv3, chunkGenerator, arg3, list, random, i + 1, j);
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
		class_3790 create(class_3485 arg, class_3784 arg2, BlockPos blockPos, int i, Rotation rotation);
	}
}

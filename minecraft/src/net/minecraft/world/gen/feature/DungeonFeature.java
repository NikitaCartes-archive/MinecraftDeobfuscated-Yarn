package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.loot.LootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature<DefaultFeatureConfig> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EntityType<?>[] field_13699 = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
	private static final BlockState field_13698 = Blocks.field_10543.getDefaultState();

	public DungeonFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13548(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		int i = 3;
		int j = random.nextInt(2) + 2;
		int k = -j - 1;
		int l = j + 1;
		int m = -1;
		int n = 4;
		int o = random.nextInt(2) + 2;
		int p = -o - 1;
		int q = o + 1;
		int r = 0;

		for (int s = k; s <= l; s++) {
			for (int t = -1; t <= 4; t++) {
				for (int u = p; u <= q; u++) {
					BlockPos blockPos2 = blockPos.add(s, t, u);
					Material material = iWorld.getBlockState(blockPos2).getMaterial();
					boolean bl = material.method_15799();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && iWorld.isAir(blockPos2) && iWorld.isAir(blockPos2.up())) {
						r++;
					}
				}
			}
		}

		if (r >= 1 && r <= 5) {
			for (int s = k; s <= l; s++) {
				for (int t = 3; t >= -1; t--) {
					for (int u = p; u <= q; u++) {
						BlockPos blockPos2x = blockPos.add(s, t, u);
						if (s != k && t != -1 && u != p && s != l && t != 4 && u != q) {
							if (iWorld.getBlockState(blockPos2x).getBlock() != Blocks.field_10034) {
								iWorld.setBlockState(blockPos2x, field_13698, 2);
							}
						} else if (blockPos2x.getY() >= 0 && !iWorld.getBlockState(blockPos2x.down()).getMaterial().method_15799()) {
							iWorld.setBlockState(blockPos2x, field_13698, 2);
						} else if (iWorld.getBlockState(blockPos2x).getMaterial().method_15799() && iWorld.getBlockState(blockPos2x).getBlock() != Blocks.field_10034) {
							if (t == -1 && random.nextInt(4) != 0) {
								iWorld.setBlockState(blockPos2x, Blocks.field_9989.getDefaultState(), 2);
							} else {
								iWorld.setBlockState(blockPos2x, Blocks.field_10445.getDefaultState(), 2);
							}
						}
					}
				}
			}

			for (int s = 0; s < 2; s++) {
				for (int t = 0; t < 3; t++) {
					int ux = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
					int v = blockPos.getY();
					int w = blockPos.getZ() + random.nextInt(o * 2 + 1) - o;
					BlockPos blockPos3 = new BlockPos(ux, v, w);
					if (iWorld.isAir(blockPos3)) {
						int x = 0;

						for (Direction direction : Direction.Type.HORIZONTAL) {
							if (iWorld.getBlockState(blockPos3.offset(direction)).getMaterial().method_15799()) {
								x++;
							}
						}

						if (x == 1) {
							iWorld.setBlockState(blockPos3, StructurePiece.method_14916(iWorld, blockPos3, Blocks.field_10034.getDefaultState()), 2);
							LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos3, LootTables.CHEST_SIMPLE_DUNGEON);
							break;
						}
					}
				}
			}

			iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof MobSpawnerBlockEntity) {
				((MobSpawnerBlockEntity)blockEntity).getLogic().method_8274(this.method_13547(random));
			} else {
				LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}

			return true;
		} else {
			return false;
		}
	}

	private EntityType<?> method_13547(Random random) {
		return field_13699[random.nextInt(field_13699.length)];
	}
}

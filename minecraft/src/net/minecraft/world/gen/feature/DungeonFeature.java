package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature<DefaultFeatureConfig> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EntityType<?>[] MOB_SPAWNER_ENTITIES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

	public DungeonFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
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
					Material material = serverWorldAccess.getBlockState(blockPos2).getMaterial();
					boolean bl = material.isSolid();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && serverWorldAccess.isAir(blockPos2) && serverWorldAccess.isAir(blockPos2.up())) {
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
						BlockState blockState = serverWorldAccess.getBlockState(blockPos2x);
						if (s != k && t != -1 && u != p && s != l && t != 4 && u != q) {
							if (!blockState.isOf(Blocks.CHEST) && !blockState.isOf(Blocks.SPAWNER)) {
								serverWorldAccess.setBlockState(blockPos2x, AIR, 2);
							}
						} else if (blockPos2x.getY() >= 0 && !serverWorldAccess.getBlockState(blockPos2x.down()).getMaterial().isSolid()) {
							serverWorldAccess.setBlockState(blockPos2x, AIR, 2);
						} else if (blockState.getMaterial().isSolid() && !blockState.isOf(Blocks.CHEST)) {
							if (t == -1 && random.nextInt(4) != 0) {
								serverWorldAccess.setBlockState(blockPos2x, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
							} else {
								serverWorldAccess.setBlockState(blockPos2x, Blocks.COBBLESTONE.getDefaultState(), 2);
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
					if (serverWorldAccess.isAir(blockPos3)) {
						int x = 0;

						for (Direction direction : Direction.Type.HORIZONTAL) {
							if (serverWorldAccess.getBlockState(blockPos3.offset(direction)).getMaterial().isSolid()) {
								x++;
							}
						}

						if (x == 1) {
							serverWorldAccess.setBlockState(blockPos3, StructurePiece.method_14916(serverWorldAccess, blockPos3, Blocks.CHEST.getDefaultState()), 2);
							LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, blockPos3, LootTables.SIMPLE_DUNGEON_CHEST);
							break;
						}
					}
				}
			}

			serverWorldAccess.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
			BlockEntity blockEntity = serverWorldAccess.getBlockEntity(blockPos);
			if (blockEntity instanceof MobSpawnerBlockEntity) {
				((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(this.getMobSpawnerEntity(random));
			} else {
				LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}

			return true;
		} else {
			return false;
		}
	}

	private EntityType<?> getMobSpawnerEntity(Random random) {
		return Util.getRandom(MOB_SPAWNER_ENTITIES, random);
	}
}

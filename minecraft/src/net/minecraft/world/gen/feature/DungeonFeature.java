package net.minecraft.world.gen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.slf4j.Logger;

public class DungeonFeature extends Feature<DefaultFeatureConfig> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final EntityType<?>[] MOB_SPAWNER_ENTITIES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
	private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

	public DungeonFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Predicate<BlockState> predicate = Feature.notInBlockTagPredicate(BlockTags.FEATURES_CANNOT_REPLACE);
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
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
					boolean bl = structureWorldAccess.getBlockState(blockPos2).isSolid();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && structureWorldAccess.isAir(blockPos2) && structureWorldAccess.isAir(blockPos2.up())) {
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
						BlockState blockState = structureWorldAccess.getBlockState(blockPos2x);
						if (s == k || t == -1 || u == p || s == l || t == 4 || u == q) {
							if (blockPos2x.getY() >= structureWorldAccess.getBottomY() && !structureWorldAccess.getBlockState(blockPos2x.down()).isSolid()) {
								structureWorldAccess.setBlockState(blockPos2x, AIR, Block.NOTIFY_LISTENERS);
							} else if (blockState.isSolid() && !blockState.isOf(Blocks.CHEST)) {
								if (t == -1 && random.nextInt(4) != 0) {
									this.setBlockStateIf(structureWorldAccess, blockPos2x, Blocks.MOSSY_COBBLESTONE.getDefaultState(), predicate);
								} else {
									this.setBlockStateIf(structureWorldAccess, blockPos2x, Blocks.COBBLESTONE.getDefaultState(), predicate);
								}
							}
						} else if (!blockState.isOf(Blocks.CHEST) && !blockState.isOf(Blocks.SPAWNER)) {
							this.setBlockStateIf(structureWorldAccess, blockPos2x, AIR, predicate);
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
					if (structureWorldAccess.isAir(blockPos3)) {
						int x = 0;

						for (Direction direction : Direction.Type.HORIZONTAL) {
							if (structureWorldAccess.getBlockState(blockPos3.offset(direction)).isSolid()) {
								x++;
							}
						}

						if (x == 1) {
							this.setBlockStateIf(
								structureWorldAccess, blockPos3, StructurePiece.orientateChest(structureWorldAccess, blockPos3, Blocks.CHEST.getDefaultState()), predicate
							);
							LootableContainerBlockEntity.setLootTable(structureWorldAccess, random, blockPos3, LootTables.SIMPLE_DUNGEON_CHEST);
							break;
						}
					}
				}
			}

			this.setBlockStateIf(structureWorldAccess, blockPos, Blocks.SPAWNER.getDefaultState(), predicate);
			if (structureWorldAccess.getBlockEntity(blockPos) instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
				mobSpawnerBlockEntity.setEntityType(this.getMobSpawnerEntity(random), random);
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

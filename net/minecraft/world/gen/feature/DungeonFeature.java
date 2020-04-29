/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.loot.LootTables;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature
extends Feature<DefaultFeatureConfig> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityType<?>[] MOB_SPAWNER_ENTITIES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();

    public DungeonFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockPos blockPos2;
        int u;
        int t;
        int s;
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
        for (s = k; s <= l; ++s) {
            for (t = -1; t <= 4; ++t) {
                for (u = p; u <= q; ++u) {
                    blockPos2 = blockPos.add(s, t, u);
                    Material material = iWorld.getBlockState(blockPos2).getMaterial();
                    boolean bl = material.isSolid();
                    if (t == -1 && !bl) {
                        return false;
                    }
                    if (t == 4 && !bl) {
                        return false;
                    }
                    if (s != k && s != l && u != p && u != q || t != 0 || !iWorld.isAir(blockPos2) || !iWorld.isAir(blockPos2.up())) continue;
                    ++r;
                }
            }
        }
        if (r < 1 || r > 5) {
            return false;
        }
        for (s = k; s <= l; ++s) {
            for (t = 3; t >= -1; --t) {
                for (u = p; u <= q; ++u) {
                    blockPos2 = blockPos.add(s, t, u);
                    if (s == k || t == -1 || u == p || s == l || t == 4 || u == q) {
                        if (blockPos2.getY() >= 0 && !iWorld.getBlockState(blockPos2.down()).getMaterial().isSolid()) {
                            iWorld.setBlockState(blockPos2, AIR, 2);
                            continue;
                        }
                        if (!iWorld.getBlockState(blockPos2).getMaterial().isSolid() || iWorld.getBlockState(blockPos2).isOf(Blocks.CHEST)) continue;
                        if (t == -1 && random.nextInt(4) != 0) {
                            iWorld.setBlockState(blockPos2, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                            continue;
                        }
                        iWorld.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
                        continue;
                    }
                    if (iWorld.getBlockState(blockPos2).isOf(Blocks.CHEST)) continue;
                    iWorld.setBlockState(blockPos2, AIR, 2);
                }
            }
        }
        block6: for (s = 0; s < 2; ++s) {
            for (t = 0; t < 3; ++t) {
                int w;
                int v;
                u = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
                BlockPos blockPos3 = new BlockPos(u, v = blockPos.getY(), w = blockPos.getZ() + random.nextInt(o * 2 + 1) - o);
                if (!iWorld.isAir(blockPos3)) continue;
                int x = 0;
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    if (!iWorld.getBlockState(blockPos3.offset(direction)).getMaterial().isSolid()) continue;
                    ++x;
                }
                if (x != 1) continue;
                iWorld.setBlockState(blockPos3, StructurePiece.method_14916(iWorld, blockPos3, Blocks.CHEST.getDefaultState()), 2);
                LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos3, LootTables.SIMPLE_DUNGEON_CHEST);
                continue block6;
            }
        }
        iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
        BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
        if (blockEntity instanceof MobSpawnerBlockEntity) {
            ((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(this.getMobSpawnerEntity(random));
        } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", (Object)blockPos.getX(), (Object)blockPos.getY(), (Object)blockPos.getZ());
        }
        return true;
    }

    private EntityType<?> getMobSpawnerEntity(Random random) {
        return Util.getRandom(MOB_SPAWNER_ENTITIES, random);
    }
}


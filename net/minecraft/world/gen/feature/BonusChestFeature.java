/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BonusChestFeature
extends Feature<DefaultFeatureConfig> {
    public BonusChestFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List list = IntStream.rangeClosed(chunkPos.getStartX(), chunkPos.getEndX()).boxed().collect(Collectors.toList());
        Collections.shuffle(list, random);
        List list2 = IntStream.rangeClosed(chunkPos.getStartZ(), chunkPos.getEndZ()).boxed().collect(Collectors.toList());
        Collections.shuffle(list2, random);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Integer integer : list) {
            for (Integer integer2 : list2) {
                mutable.set(integer, 0, integer2);
                BlockPos blockPos2 = iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable);
                if (!iWorld.isAir(blockPos2) && !iWorld.getBlockState(blockPos2).getCollisionShape(iWorld, blockPos2).isEmpty()) continue;
                iWorld.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
                LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos2, LootTables.SPAWN_BONUS_CHEST);
                BlockState blockState = Blocks.TORCH.getDefaultState();
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    BlockPos blockPos3 = blockPos2.offset(direction);
                    if (!blockState.canPlaceAt(iWorld, blockPos3)) continue;
                    iWorld.setBlockState(blockPos3, blockState, 2);
                }
                return true;
            }
        }
        return false;
    }
}


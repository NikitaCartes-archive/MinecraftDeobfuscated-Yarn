/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class LargeEntitySpawnHelper {
    public static <T extends MobEntity> Optional<T> trySpawnAt(EntityType<T> entityType, SpawnReason spawnReason, ServerWorld serverWorld, BlockPos blockPos, int i, int j, int k) {
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (int l = 0; l < i; ++l) {
            MobEntity mobEntity;
            int n;
            int m = MathHelper.nextBetween(serverWorld.random, -j, j);
            if (!LargeEntitySpawnHelper.findSpawnPos(serverWorld, k, mutable.set(blockPos, m, k, n = MathHelper.nextBetween(serverWorld.random, -j, j))) || (mobEntity = (MobEntity)entityType.create(serverWorld, null, null, null, mutable, spawnReason, false, false)) == null) continue;
            if (mobEntity.canSpawn(serverWorld, spawnReason) && mobEntity.canSpawn(serverWorld)) {
                serverWorld.spawnEntityAndPassengers(mobEntity);
                return Optional.of(mobEntity);
            }
            mobEntity.discard();
        }
        return Optional.empty();
    }

    private static boolean findSpawnPos(ServerWorld serverWorld, int i, BlockPos.Mutable mutable) {
        if (!serverWorld.getWorldBorder().contains(mutable)) {
            return false;
        }
        BlockState blockState = serverWorld.getBlockState(mutable);
        for (int j = i; j >= -i; --j) {
            mutable.move(Direction.DOWN);
            BlockState blockState2 = serverWorld.getBlockState(mutable);
            if ((blockState.isAir() || blockState.getMaterial().isLiquid()) && blockState2.getMaterial().blocksLight()) {
                mutable.move(Direction.UP);
                return true;
            }
            blockState = blockState2;
        }
        return false;
    }
}


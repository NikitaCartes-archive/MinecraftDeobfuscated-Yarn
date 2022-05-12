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
    public static <T extends MobEntity> Optional<T> trySpawnAt(EntityType<T> entityType, SpawnReason reason, ServerWorld world, BlockPos pos, int tries, int horizontalRange, int verticalRange) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < tries; ++i) {
            MobEntity mobEntity;
            int k;
            int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            if (!LargeEntitySpawnHelper.findSpawnPos(world, verticalRange, mutable.set(pos, j, verticalRange, k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange))) || (mobEntity = (MobEntity)entityType.create(world, null, null, null, mutable, reason, false, false)) == null) continue;
            if (mobEntity.canSpawn(world, reason) && mobEntity.canSpawn(world)) {
                world.spawnEntityAndPassengers(mobEntity);
                return Optional.of(mobEntity);
            }
            mobEntity.discard();
        }
        return Optional.empty();
    }

    private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos) {
        if (!world.getWorldBorder().contains(pos)) {
            return false;
        }
        boolean bl = world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
        for (int i = verticalRange; i >= -verticalRange; --i) {
            pos.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(pos);
            boolean bl2 = blockState.getCollisionShape(world, pos).isEmpty();
            if (bl && !bl2) {
                pos.move(Direction.UP);
                return true;
            }
            bl = bl2;
        }
        return false;
    }
}


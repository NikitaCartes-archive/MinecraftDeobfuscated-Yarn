/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class LargeEntitySpawnHelper {
    public static <T extends MobEntity> Optional<T> trySpawnAt(EntityType<T> entityType, SpawnReason reason, ServerWorld world, BlockPos pos, int tries, int horizontalRange, int verticalRange, Requirements requirements) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < tries; ++i) {
            MobEntity mobEntity;
            int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            int k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            mutable.set(pos, j, verticalRange, k);
            if (!world.getWorldBorder().contains(mutable) || !LargeEntitySpawnHelper.findSpawnPos(world, verticalRange, mutable, requirements) || (mobEntity = (MobEntity)entityType.create(world, null, null, mutable, reason, false, false)) == null) continue;
            if (mobEntity.canSpawn(world, reason) && mobEntity.canSpawn(world)) {
                world.spawnEntityAndPassengers(mobEntity);
                return Optional.of(mobEntity);
            }
            mobEntity.discard();
        }
        return Optional.empty();
    }

    private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos, Requirements requirements) {
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);
        BlockState blockState = world.getBlockState(mutable);
        for (int i = verticalRange; i >= -verticalRange; --i) {
            pos.move(Direction.DOWN);
            mutable.set((Vec3i)pos, Direction.UP);
            BlockState blockState2 = world.getBlockState(pos);
            if (requirements.canSpawnOn(world, pos, blockState2, mutable, blockState)) {
                pos.move(Direction.UP);
                return true;
            }
            blockState = blockState2;
        }
        return false;
    }

    public static interface Requirements {
        public static final Requirements IRON_GOLEM = (world, pos, state, abovePos, aboveState) -> (aboveState.isAir() || aboveState.getMaterial().isLiquid()) && state.getMaterial().blocksLight();
        public static final Requirements WARDEN = (world, pos, state, abovePos, aboveState) -> aboveState.getCollisionShape(world, abovePos).isEmpty() && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);

        public boolean canSpawnOn(ServerWorld var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5);
    }
}


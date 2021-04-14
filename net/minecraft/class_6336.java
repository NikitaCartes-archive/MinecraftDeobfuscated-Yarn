/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_6336<E extends PathAwareEntity>
extends Task<E> {
    public static final int field_33461 = 160;
    private final int field_33462;
    private final int field_33463;
    private final int field_33464;
    private final float field_33465;
    private final TargetPredicate field_33466;
    private final int field_33467;
    private final Function<E, SoundEvent> field_33468;
    private Optional<Long> field_33469 = Optional.empty();
    private Optional<class_6337> field_33470 = Optional.empty();

    public class_6336(int i, int j, int k, float f, TargetPredicate targetPredicate, int l, Function<E, SoundEvent> function) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_ABSENT), 160);
        this.field_33462 = i;
        this.field_33463 = j;
        this.field_33464 = k;
        this.field_33465 = f;
        this.field_33466 = targetPredicate;
        this.field_33467 = l;
        this.field_33468 = function;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Brain<?> brain = pathAwareEntity.getBrain();
        brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).flatMap(list -> list.stream().filter(livingEntity -> this.field_33466.test(pathAwareEntity, (LivingEntity)livingEntity)).findFirst()).ifPresent(livingEntity -> this.method_36268(pathAwareEntity, (LivingEntity)livingEntity));
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Brain<Vec3d> brain = pathAwareEntity.getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.RAM_TARGET)) {
            serverWorld.sendEntityStatus(pathAwareEntity, (byte)59);
            brain.remember(MemoryModuleType.RAM_COOLDOWN_TICKS, this.field_33462);
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return this.field_33470.isPresent() && this.field_33470.get().method_36276().isAlive();
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
        boolean bl;
        if (!this.field_33470.isPresent()) {
            return;
        }
        ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.field_33470.get().method_36273(), this.field_33465, 0));
        ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.field_33470.get().method_36276(), true));
        boolean bl2 = bl = !this.field_33470.get().method_36276().getBlockPos().equals(this.field_33470.get().method_36275());
        if (bl) {
            serverWorld.sendEntityStatus((Entity)pathAwareEntity, (byte)59);
            ((MobEntity)pathAwareEntity).getNavigation().stop();
            this.method_36268((PathAwareEntity)pathAwareEntity, this.field_33470.get().field_33473);
        } else {
            BlockPos blockPos = ((Entity)pathAwareEntity).getBlockPos();
            if (blockPos.equals(this.field_33470.get().method_36273())) {
                serverWorld.sendEntityStatus((Entity)pathAwareEntity, (byte)58);
                if (!this.field_33469.isPresent()) {
                    this.field_33469 = Optional.of(l);
                }
                if (l - this.field_33469.get() >= (long)this.field_33467) {
                    ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.RAM_TARGET, this.method_36266(blockPos, this.field_33470.get().method_36275()));
                    serverWorld.playSoundFromEntity(null, (Entity)pathAwareEntity, this.field_33468.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0f, 1.0f);
                    this.field_33470 = Optional.empty();
                }
            }
        }
    }

    private Vec3d method_36266(BlockPos blockPos, BlockPos blockPos2) {
        double d = 0.5;
        double e = 0.5 * (double)MathHelper.sign(blockPos2.getX() - blockPos.getX());
        double f = 0.5 * (double)MathHelper.sign(blockPos2.getZ() - blockPos.getZ());
        return Vec3d.ofBottomCenter(blockPos2).add(e, 0.0, f);
    }

    private Optional<BlockPos> method_36262(PathAwareEntity pathAwareEntity, LivingEntity livingEntity) {
        BlockPos blockPos2 = livingEntity.getBlockPos();
        if (!this.method_36263(pathAwareEntity, blockPos2)) {
            return Optional.empty();
        }
        ArrayList<BlockPos> list = Lists.newArrayList();
        BlockPos.Mutable mutable = blockPos2.mutableCopy();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            mutable.set(blockPos2);
            for (int i = 0; i < this.field_33464; ++i) {
                if (this.method_36263(pathAwareEntity, mutable.move(direction))) continue;
                mutable.move(direction.getOpposite());
                break;
            }
            if (mutable.getManhattanDistance(blockPos2) < this.field_33463) continue;
            list.add(mutable.toImmutable());
        }
        EntityNavigation entityNavigation = pathAwareEntity.getNavigation();
        return list.stream().sorted(Comparator.comparingDouble(pathAwareEntity.getBlockPos()::getSquaredDistance)).filter(blockPos -> {
            Path path = entityNavigation.findPathTo((BlockPos)blockPos, 0);
            return path != null && path.reachesTarget();
        }).findFirst();
    }

    private boolean method_36263(PathAwareEntity pathAwareEntity, BlockPos blockPos) {
        return pathAwareEntity.getNavigation().isValidPosition(blockPos) && pathAwareEntity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(pathAwareEntity.world, blockPos.mutableCopy())) == 0.0f;
    }

    private void method_36268(PathAwareEntity pathAwareEntity, LivingEntity livingEntity) {
        this.field_33469 = Optional.empty();
        this.field_33470 = this.method_36262(pathAwareEntity, livingEntity).map(blockPos -> new class_6337((BlockPos)blockPos, livingEntity.getBlockPos(), livingEntity));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (PathAwareEntity)entity, time);
    }

    public static class class_6337 {
        private final BlockPos field_33471;
        private final BlockPos field_33472;
        private final LivingEntity field_33473;

        public class_6337(BlockPos blockPos, BlockPos blockPos2, LivingEntity livingEntity) {
            this.field_33471 = blockPos;
            this.field_33472 = blockPos2;
            this.field_33473 = livingEntity;
        }

        public BlockPos method_36273() {
            return this.field_33471;
        }

        public BlockPos method_36275() {
            return this.field_33472;
        }

        public LivingEntity method_36276() {
            return this.field_33473;
        }
    }
}


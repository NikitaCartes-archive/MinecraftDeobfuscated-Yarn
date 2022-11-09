/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
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

/**
 * The task where an entity finds a target, a place to start its ram, and wait to
 * start its ram.
 * 
 * @param <E> the task owner, usually a goat
 */
public class PrepareRamTask<E extends PathAwareEntity>
extends MultiTickTask<E> {
    public static final int RUN_TIME = 160;
    private final ToIntFunction<E> cooldownFactory;
    private final int minRamDistance;
    private final int maxRamDistance;
    private final float speed;
    private final TargetPredicate targetPredicate;
    private final int prepareTime;
    private final Function<E, SoundEvent> soundFactory;
    private Optional<Long> prepareStartTime = Optional.empty();
    private Optional<Ram> ram = Optional.empty();

    /**
     * Creates a ram preparation task. It has a fixed run time of {@value #RUN_TIME}.
     * 
     * @param speed the speed the entity walks at
     * @param targetPredicate the predicate for the ram target
     * @param prepareTime the prepare time, in ticks, the entity will stand between reaching the ram
     * start position and the start of the ram
     * @param soundFactory the factory of the ram prepare sound given the entity
     * @param cooldownFactory the factory for the cooldown, in ticks, of the ram task, given the entity
     * @param minDistance the min manhattan distance from the ram starting location to the ram target
     * @param maxDistance the max straight-line distance from the ram starting location to the ram target
     */
    public PrepareRamTask(ToIntFunction<E> cooldownFactory, int minDistance, int maxDistance, float speed, TargetPredicate targetPredicate, int prepareTime, Function<E, SoundEvent> soundFactory) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_ABSENT), 160);
        this.cooldownFactory = cooldownFactory;
        this.minRamDistance = minDistance;
        this.maxRamDistance = maxDistance;
        this.speed = speed;
        this.targetPredicate = targetPredicate;
        this.prepareTime = prepareTime;
        this.soundFactory = soundFactory;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        Brain<?> brain = pathAwareEntity.getBrain();
        brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).flatMap(mob2 -> mob2.findFirst(mob -> this.targetPredicate.test(pathAwareEntity, (LivingEntity)mob))).ifPresent(mob -> this.findRam(pathAwareEntity, (LivingEntity)mob));
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
        Brain<Vec3d> brain = ((LivingEntity)pathAwareEntity).getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.RAM_TARGET)) {
            serverWorld.sendEntityStatus((Entity)pathAwareEntity, EntityStatuses.FINISH_RAM);
            brain.remember(MemoryModuleType.RAM_COOLDOWN_TICKS, this.cooldownFactory.applyAsInt(pathAwareEntity));
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return this.ram.isPresent() && this.ram.get().getEntity().isAlive();
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
        boolean bl;
        if (this.ram.isEmpty()) {
            return;
        }
        ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(this.ram.get().getStart(), this.speed, 0));
        ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.ram.get().getEntity(), true));
        boolean bl2 = bl = !this.ram.get().getEntity().getBlockPos().equals(this.ram.get().getEnd());
        if (bl) {
            serverWorld.sendEntityStatus((Entity)pathAwareEntity, EntityStatuses.FINISH_RAM);
            ((MobEntity)pathAwareEntity).getNavigation().stop();
            this.findRam((PathAwareEntity)pathAwareEntity, this.ram.get().entity);
        } else {
            BlockPos blockPos = ((Entity)pathAwareEntity).getBlockPos();
            if (blockPos.equals(this.ram.get().getStart())) {
                serverWorld.sendEntityStatus((Entity)pathAwareEntity, EntityStatuses.PREPARE_RAM);
                if (this.prepareStartTime.isEmpty()) {
                    this.prepareStartTime = Optional.of(l);
                }
                if (l - this.prepareStartTime.get() >= (long)this.prepareTime) {
                    ((LivingEntity)pathAwareEntity).getBrain().remember(MemoryModuleType.RAM_TARGET, this.calculateRamTarget(blockPos, this.ram.get().getEnd()));
                    serverWorld.playSoundFromEntity(null, (Entity)pathAwareEntity, this.soundFactory.apply(pathAwareEntity), SoundCategory.NEUTRAL, 1.0f, ((LivingEntity)pathAwareEntity).getSoundPitch());
                    this.ram = Optional.empty();
                }
            }
        }
    }

    private Vec3d calculateRamTarget(BlockPos start, BlockPos end) {
        double d = 0.5;
        double e = 0.5 * (double)MathHelper.sign(end.getX() - start.getX());
        double f = 0.5 * (double)MathHelper.sign(end.getZ() - start.getZ());
        return Vec3d.ofBottomCenter(end).add(e, 0.0, f);
    }

    private Optional<BlockPos> findRamStart(PathAwareEntity entity, LivingEntity target) {
        BlockPos blockPos = target.getBlockPos();
        if (!this.canReach(entity, blockPos)) {
            return Optional.empty();
        }
        ArrayList<BlockPos> list = Lists.newArrayList();
        BlockPos.Mutable mutable = blockPos.mutableCopy();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            mutable.set(blockPos);
            for (int i = 0; i < this.maxRamDistance; ++i) {
                if (this.canReach(entity, mutable.move(direction))) continue;
                mutable.move(direction.getOpposite());
                break;
            }
            if (mutable.getManhattanDistance(blockPos) < this.minRamDistance) continue;
            list.add(mutable.toImmutable());
        }
        EntityNavigation entityNavigation = entity.getNavigation();
        return list.stream().sorted(Comparator.comparingDouble(entity.getBlockPos()::getSquaredDistance)).filter(start -> {
            Path path = entityNavigation.findPathTo((BlockPos)start, 0);
            return path != null && path.reachesTarget();
        }).findFirst();
    }

    private boolean canReach(PathAwareEntity entity, BlockPos target) {
        return entity.getNavigation().isValidPosition(target) && entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity.world, target.mutableCopy())) == 0.0f;
    }

    private void findRam(PathAwareEntity entity, LivingEntity target) {
        this.prepareStartTime = Optional.empty();
        this.ram = this.findRamStart(entity, target).map(start -> new Ram((BlockPos)start, target.getBlockPos(), target));
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (E)((PathAwareEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }

    public static class Ram {
        private final BlockPos start;
        private final BlockPos end;
        final LivingEntity entity;

        public Ram(BlockPos start, BlockPos end, LivingEntity entity) {
            this.start = start;
            this.end = end;
            this.entity = entity;
        }

        public BlockPos getStart() {
            return this.start;
        }

        public BlockPos getEnd() {
            return this.end;
        }

        public LivingEntity getEntity() {
            return this.entity;
        }
    }
}


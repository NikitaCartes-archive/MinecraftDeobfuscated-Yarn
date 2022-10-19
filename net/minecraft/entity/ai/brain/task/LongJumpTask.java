/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LongJumpTask<E extends MobEntity>
extends Task<E> {
    protected static final int MAX_COOLDOWN = 20;
    private static final int TARGET_RETAIN_TIME = 40;
    protected static final int PATHING_DISTANCE = 8;
    private static final int RUN_TIME = 200;
    private static final List<Integer> RAM_RANGES = Lists.newArrayList(65, 70, 75, 80);
    private final UniformIntProvider cooldownRange;
    protected final int verticalRange;
    protected final int horizontalRange;
    protected final float maxRange;
    protected List<Target> targets = Lists.newArrayList();
    protected Optional<Vec3d> lastPos = Optional.empty();
    @Nullable
    protected Vec3d lastTarget;
    protected int cooldown;
    protected long targetTime;
    private final Function<E, SoundEvent> entityToSound;
    private final BiPredicate<E, BlockPos> jumpToPredicate;

    public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound) {
        this(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, LongJumpTask::shouldJumpTo);
    }

    public static <E extends MobEntity> boolean shouldJumpTo(E entity, BlockPos pos) {
        World world = entity.world;
        BlockPos blockPos = pos.down();
        return world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos) && entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(world, pos.mutableCopy())) == 0.0f;
    }

    public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound, BiPredicate<E, BlockPos> jumpToPredicate) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), 200);
        this.cooldownRange = cooldownRange;
        this.verticalRange = verticalRange;
        this.horizontalRange = horizontalRange;
        this.maxRange = maxRange;
        this.entityToSound = entityToSound;
        this.jumpToPredicate = jumpToPredicate;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        boolean bl;
        boolean bl2 = bl = mobEntity.isOnGround() && !mobEntity.isTouchingWater() && !mobEntity.isInLava() && !serverWorld.getBlockState(mobEntity.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
        if (!bl) {
            mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
        }
        return bl;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        boolean bl;
        boolean bl2 = bl = this.lastPos.isPresent() && this.lastPos.get().equals(mobEntity.getPos()) && this.cooldown > 0 && !mobEntity.isInsideWaterOrBubbleColumn() && (this.lastTarget != null || !this.targets.isEmpty());
        if (!bl && mobEntity.getBrain().getOptionalMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random) / 2);
            mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        }
        return bl;
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        this.lastTarget = null;
        this.cooldown = 20;
        this.lastPos = Optional.of(((Entity)mobEntity).getPos());
        BlockPos blockPos = ((Entity)mobEntity).getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        this.targets = BlockPos.stream(i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange).filter(blockPos2 -> !blockPos2.equals(blockPos)).map(blockPos2 -> new Target(blockPos2.toImmutable(), MathHelper.ceil(blockPos.getSquaredDistance((Vec3i)blockPos2)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        if (this.lastTarget != null) {
            if (l - this.targetTime >= 40L) {
                ((Entity)mobEntity).setYaw(((MobEntity)mobEntity).bodyYaw);
                ((LivingEntity)mobEntity).setNoDrag(true);
                double d = this.lastTarget.length();
                double e = d + ((LivingEntity)mobEntity).getJumpBoostVelocityModifier();
                ((Entity)mobEntity).setVelocity(this.lastTarget.multiply(e / d));
                ((LivingEntity)mobEntity).getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                serverWorld.playSoundFromEntity(null, (Entity)mobEntity, this.entityToSound.apply(mobEntity), SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
        } else {
            --this.cooldown;
            this.findTarget(serverWorld, mobEntity, l);
        }
    }

    protected void findTarget(ServerWorld world, E entity, long time) {
        while (!this.targets.isEmpty()) {
            Vec3d vec3d;
            Vec3d vec3d2;
            Target target;
            BlockPos blockPos;
            Optional<Target> optional = this.getTarget(world);
            if (optional.isEmpty() || !this.canJumpTo(world, entity, blockPos = (target = optional.get()).getPos()) || (vec3d2 = this.getJumpingVelocity((MobEntity)entity, vec3d = Vec3d.ofCenter(blockPos))) == null) continue;
            ((LivingEntity)entity).getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(blockPos));
            EntityNavigation entityNavigation = ((MobEntity)entity).getNavigation();
            Path path = entityNavigation.findPathTo(blockPos, 0, 8);
            if (path != null && path.reachesTarget()) continue;
            this.lastTarget = vec3d2;
            this.targetTime = time;
            return;
        }
    }

    protected Optional<Target> getTarget(ServerWorld world) {
        Optional<Target> optional = Weighting.getRandom(world.random, this.targets);
        optional.ifPresent(this.targets::remove);
        return optional;
    }

    private boolean canJumpTo(ServerWorld world, E entity, BlockPos pos) {
        BlockPos blockPos = ((Entity)entity).getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getZ();
        if (i == pos.getX() && j == pos.getZ()) {
            return false;
        }
        return this.jumpToPredicate.test(entity, pos);
    }

    @Nullable
    protected Vec3d getJumpingVelocity(MobEntity entity, Vec3d pos) {
        ArrayList<Integer> list = Lists.newArrayList(RAM_RANGES);
        Collections.shuffle(list);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            Vec3d vec3d = this.getJumpingVelocity(entity, pos, i);
            if (vec3d == null) continue;
            return vec3d;
        }
        return null;
    }

    @Nullable
    private Vec3d getJumpingVelocity(MobEntity entity, Vec3d pos, int range) {
        Vec3d vec3d = entity.getPos();
        Vec3d vec3d2 = new Vec3d(pos.x - vec3d.x, 0.0, pos.z - vec3d.z).normalize().multiply(0.5);
        pos = pos.subtract(vec3d2);
        Vec3d vec3d3 = pos.subtract(vec3d);
        float f = (float)range * (float)Math.PI / 180.0f;
        double d = Math.atan2(vec3d3.z, vec3d3.x);
        double e = vec3d3.subtract(0.0, vec3d3.y, 0.0).lengthSquared();
        double g = Math.sqrt(e);
        double h = vec3d3.y;
        double i = Math.sin(2.0f * f);
        double j = 0.08;
        double k = Math.pow(Math.cos(f), 2.0);
        double l = Math.sin(f);
        double m = Math.cos(f);
        double n = Math.sin(d);
        double o = Math.cos(d);
        double p = e * 0.08 / (g * i - 2.0 * h * k);
        if (p < 0.0) {
            return null;
        }
        double q = Math.sqrt(p);
        if (q > (double)this.maxRange) {
            return null;
        }
        double r = q * m;
        double s = q * l;
        int t = MathHelper.ceil(g / r) * 2;
        double u = 0.0;
        Vec3d vec3d4 = null;
        EntityDimensions entityDimensions = entity.getDimensions(EntityPose.LONG_JUMPING);
        for (int v = 0; v < t - 1; ++v) {
            double w = l / m * (u += g / (double)t) - Math.pow(u, 2.0) * 0.08 / (2.0 * p * Math.pow(m, 2.0));
            double x = u * o;
            double y = u * n;
            Vec3d vec3d5 = new Vec3d(vec3d.x + x, vec3d.y + w, vec3d.z + y);
            if (vec3d4 != null && !this.canReach(entity, entityDimensions, vec3d4, vec3d5)) {
                return null;
            }
            vec3d4 = vec3d5;
        }
        return new Vec3d(r * o, s, r * n).multiply(0.95f);
    }

    private boolean canReach(MobEntity entity, EntityDimensions dimensions, Vec3d vec3d, Vec3d vec3d2) {
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        double d = Math.min(dimensions.width, dimensions.height);
        int i = MathHelper.ceil(vec3d3.length() / d);
        Vec3d vec3d4 = vec3d3.normalize();
        Vec3d vec3d5 = vec3d;
        for (int j = 0; j < i; ++j) {
            Vec3d vec3d6 = vec3d5 = j == i - 1 ? vec3d2 : vec3d5.add(vec3d4.multiply(d * (double)0.9f));
            if (entity.world.isSpaceEmpty(entity, dimensions.getBoxAt(vec3d5))) continue;
            return false;
        }
        return true;
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (E)((MobEntity)entity), time);
    }

    public static class Target
    extends Weighted.Absent {
        private final BlockPos pos;

        public Target(BlockPos pos, int weight) {
            super(weight);
            this.pos = pos;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }
}


/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public abstract class EntityNavigation {
    private static final int RECALCULATE_COOLDOWN = 20;
    protected final MobEntity entity;
    protected final World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    protected int tickCount;
    protected int pathStartTime;
    protected Vec3d pathStartPos = Vec3d.ZERO;
    protected Vec3i lastNodePosition = Vec3i.ZERO;
    protected long currentNodeMs;
    protected long lastActiveTickMs;
    protected double currentNodeTimeout;
    /**
     * If the Chebyshev distance from the entity to the next node is less than
     * or equal to this value, the entity is considered "reached" the node.
     */
    protected float nodeReachProximity = 0.5f;
    protected boolean inRecalculationCooldown;
    protected long lastRecalculateTime;
    protected PathNodeMaker nodeMaker;
    @Nullable
    private BlockPos currentTarget;
    private int currentDistance;
    private float rangeMultiplier = 1.0f;
    private final PathNodeNavigator pathNodeNavigator;
    private boolean nearPathStartPos;

    public EntityNavigation(MobEntity entity, World world) {
        this.entity = entity;
        this.world = world;
        int i = MathHelper.floor(entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 16.0);
        this.pathNodeNavigator = this.createPathNodeNavigator(i);
    }

    public void resetRangeMultiplier() {
        this.rangeMultiplier = 1.0f;
    }

    public void setRangeMultiplier(float rangeMultiplier) {
        this.rangeMultiplier = rangeMultiplier;
    }

    @Nullable
    public BlockPos getTargetPos() {
        return this.currentTarget;
    }

    protected abstract PathNodeNavigator createPathNodeNavigator(int var1);

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void recalculatePath() {
        if (this.world.getTime() - this.lastRecalculateTime > 20L) {
            if (this.currentTarget != null) {
                this.currentPath = null;
                this.currentPath = this.findPathTo(this.currentTarget, this.currentDistance);
                this.lastRecalculateTime = this.world.getTime();
                this.inRecalculationCooldown = false;
            }
        } else {
            this.inRecalculationCooldown = true;
        }
    }

    @Nullable
    public final Path findPathTo(double x, double y, double z, int distance) {
        return this.findPathTo(new BlockPos(x, y, z), distance);
    }

    @Nullable
    public Path findPathToAny(Stream<BlockPos> positions, int distance) {
        return this.findPathTo(positions.collect(Collectors.toSet()), 8, false, distance);
    }

    @Nullable
    public Path findPathTo(Set<BlockPos> positions, int distance) {
        return this.findPathTo(positions, 8, false, distance);
    }

    @Nullable
    public Path findPathTo(BlockPos target, int distance) {
        return this.findPathTo(ImmutableSet.of(target), 8, false, distance);
    }

    @Nullable
    public Path findPathTo(BlockPos target, int minDistance, int maxDistance) {
        return this.findPathToAny(ImmutableSet.of(target), 8, false, minDistance, maxDistance);
    }

    @Nullable
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(ImmutableSet.of(entity.getBlockPos()), 16, true, distance);
    }

    @Nullable
    protected Path findPathTo(Set<BlockPos> positions, int range, boolean useHeadPos, int distance) {
        return this.findPathToAny(positions, range, useHeadPos, distance, (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE));
    }

    @Nullable
    protected Path findPathToAny(Set<BlockPos> positions, int range, boolean useHeadPos, int distance, float followRange) {
        if (positions.isEmpty()) {
            return null;
        }
        if (this.entity.getY() < (double)this.world.getBottomY()) {
            return null;
        }
        if (!this.isAtValidPosition()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && positions.contains(this.currentTarget)) {
            return this.currentPath;
        }
        this.world.getProfiler().push("pathfind");
        BlockPos blockPos = useHeadPos ? this.entity.getBlockPos().up() : this.entity.getBlockPos();
        int i = (int)(followRange + (float)range);
        ChunkCache chunkCache = new ChunkCache(this.world, blockPos.add(-i, -i, -i), blockPos.add(i, i, i));
        Path path = this.pathNodeNavigator.findPathToAny(chunkCache, this.entity, positions, followRange, distance, this.rangeMultiplier);
        this.world.getProfiler().pop();
        if (path != null && path.getTarget() != null) {
            this.currentTarget = path.getTarget();
            this.currentDistance = distance;
            this.resetNode();
        }
        return path;
    }

    public boolean startMovingTo(double x, double y, double z, double speed) {
        return this.startMovingAlong(this.findPathTo(x, y, z, 1), speed);
    }

    public boolean startMovingTo(Entity entity, double speed) {
        Path path = this.findPathTo(entity, 1);
        return path != null && this.startMovingAlong(path, speed);
    }

    public boolean startMovingAlong(@Nullable Path path, double speed) {
        if (path == null) {
            this.currentPath = null;
            return false;
        }
        if (!path.equalsPath(this.currentPath)) {
            this.currentPath = path;
        }
        if (this.isIdle()) {
            return false;
        }
        this.adjustPath();
        if (this.currentPath.getLength() <= 0) {
            return false;
        }
        this.speed = speed;
        Vec3d vec3d = this.getPos();
        this.pathStartTime = this.tickCount;
        this.pathStartPos = vec3d;
        return true;
    }

    @Nullable
    public Path getCurrentPath() {
        return this.currentPath;
    }

    public void tick() {
        Vec3d vec3d;
        ++this.tickCount;
        if (this.inRecalculationCooldown) {
            this.recalculatePath();
        }
        if (this.isIdle()) {
            return;
        }
        if (this.isAtValidPosition()) {
            this.continueFollowingPath();
        } else if (this.currentPath != null && !this.currentPath.isFinished()) {
            vec3d = this.getPos();
            Vec3d vec3d2 = this.currentPath.getNodePosition(this.entity);
            if (vec3d.y > vec3d2.y && !this.entity.isOnGround() && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d2.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d2.z)) {
                this.currentPath.next();
            }
        }
        DebugInfoSender.sendPathfindingData(this.world, this.entity, this.currentPath, this.nodeReachProximity);
        if (this.isIdle()) {
            return;
        }
        vec3d = this.currentPath.getNodePosition(this.entity);
        this.entity.getMoveControl().moveTo(vec3d.x, this.adjustTargetY(vec3d), vec3d.z, this.speed);
    }

    protected double adjustTargetY(Vec3d pos) {
        BlockPos blockPos = new BlockPos(pos);
        return this.world.getBlockState(blockPos.down()).isAir() ? pos.y : LandPathNodeMaker.getFeetY(this.world, blockPos);
    }

    protected void continueFollowingPath() {
        boolean bl;
        Vec3d vec3d = this.getPos();
        this.nodeReachProximity = this.entity.getWidth() > 0.75f ? this.entity.getWidth() / 2.0f : 0.75f - this.entity.getWidth() / 2.0f;
        BlockPos vec3i = this.currentPath.getCurrentNodePos();
        double d = Math.abs(this.entity.getX() - ((double)vec3i.getX() + 0.5));
        double e = Math.abs(this.entity.getY() - (double)vec3i.getY());
        double f = Math.abs(this.entity.getZ() - ((double)vec3i.getZ() + 0.5));
        boolean bl2 = bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity && e < 1.0;
        if (bl || this.entity.canJumpToNextPathNode(this.currentPath.getCurrentNode().type) && this.shouldJumpToNextNode(vec3d)) {
            this.currentPath.next();
        }
        this.checkTimeouts(vec3d);
    }

    private boolean shouldJumpToNextNode(Vec3d currentPos) {
        Vec3d vec3d4;
        if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) {
            return false;
        }
        Vec3d vec3d = Vec3d.ofBottomCenter(this.currentPath.getCurrentNodePos());
        if (!currentPos.isInRange(vec3d, 2.0)) {
            return false;
        }
        if (this.canPathDirectlyThrough(currentPos, this.currentPath.getNodePosition(this.entity))) {
            return true;
        }
        Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.getNodePos(this.currentPath.getCurrentNodeIndex() + 1));
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        return vec3d3.dotProduct(vec3d4 = currentPos.subtract(vec3d)) > 0.0;
    }

    protected void checkTimeouts(Vec3d currentPos) {
        if (this.tickCount - this.pathStartTime > 100) {
            if (currentPos.squaredDistanceTo(this.pathStartPos) < 2.25) {
                this.nearPathStartPos = true;
                this.stop();
            } else {
                this.nearPathStartPos = false;
            }
            this.pathStartTime = this.tickCount;
            this.pathStartPos = currentPos;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            BlockPos vec3i = this.currentPath.getCurrentNodePos();
            if (vec3i.equals(this.lastNodePosition)) {
                this.currentNodeMs += Util.getMeasuringTimeMs() - this.lastActiveTickMs;
            } else {
                this.lastNodePosition = vec3i;
                double d = currentPos.distanceTo(Vec3d.ofBottomCenter(this.lastNodePosition));
                double d2 = this.currentNodeTimeout = this.entity.getMovementSpeed() > 0.0f ? d / (double)this.entity.getMovementSpeed() * 1000.0 : 0.0;
            }
            if (this.currentNodeTimeout > 0.0 && (double)this.currentNodeMs > this.currentNodeTimeout * 3.0) {
                this.resetNodeAndStop();
            }
            this.lastActiveTickMs = Util.getMeasuringTimeMs();
        }
    }

    private void resetNodeAndStop() {
        this.resetNode();
        this.stop();
    }

    private void resetNode() {
        this.lastNodePosition = Vec3i.ZERO;
        this.currentNodeMs = 0L;
        this.currentNodeTimeout = 0.0;
        this.nearPathStartPos = false;
    }

    public boolean isIdle() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public boolean isFollowingPath() {
        return !this.isIdle();
    }

    public void stop() {
        this.currentPath = null;
    }

    /**
     * The position to act as if the entity is at for pathfinding purposes
     */
    protected abstract Vec3d getPos();

    protected abstract boolean isAtValidPosition();

    protected boolean isInLiquid() {
        return this.entity.isInsideWaterOrBubbleColumn() || this.entity.isInLava();
    }

    /**
     * Adjusts the current path according to various special obstacles that may be in the way, for example sunlight
     */
    protected void adjustPath() {
        if (this.currentPath == null) {
            return;
        }
        for (int i = 0; i < this.currentPath.getLength(); ++i) {
            PathNode pathNode = this.currentPath.getNode(i);
            PathNode pathNode2 = i + 1 < this.currentPath.getLength() ? this.currentPath.getNode(i + 1) : null;
            BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.x, pathNode.y, pathNode.z));
            if (!blockState.isIn(BlockTags.CAULDRONS)) continue;
            this.currentPath.setNode(i, pathNode.copyWithNewPosition(pathNode.x, pathNode.y + 1, pathNode.z));
            if (pathNode2 == null || pathNode.y < pathNode2.y) continue;
            this.currentPath.setNode(i + 1, pathNode.copyWithNewPosition(pathNode2.x, pathNode.y + 1, pathNode2.z));
        }
    }

    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
        return false;
    }

    protected static boolean doesNotCollide(MobEntity entity, Vec3d startPos, Vec3d entityPos) {
        Vec3d vec3d = new Vec3d(entityPos.x, entityPos.y + (double)entity.getHeight() * 0.5, entityPos.z);
        return entity.world.raycast(new RaycastContext(startPos, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS;
    }

    public boolean isValidPosition(BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.world.getBlockState(blockPos).isOpaqueFullCube(this.world, blockPos);
    }

    public PathNodeMaker getNodeMaker() {
        return this.nodeMaker;
    }

    public void setCanSwim(boolean canSwim) {
        this.nodeMaker.setCanSwim(canSwim);
    }

    public boolean canSwim() {
        return this.nodeMaker.canSwim();
    }

    public boolean shouldRecalculatePath(BlockPos pos) {
        if (this.inRecalculationCooldown) {
            return false;
        }
        if (this.currentPath == null || this.currentPath.isFinished() || this.currentPath.getLength() == 0) {
            return false;
        }
        PathNode pathNode = this.currentPath.getEnd();
        Vec3d vec3d = new Vec3d(((double)pathNode.x + this.entity.getX()) / 2.0, ((double)pathNode.y + this.entity.getY()) / 2.0, ((double)pathNode.z + this.entity.getZ()) / 2.0);
        return pos.isWithinDistance(vec3d, (double)(this.currentPath.getLength() - this.currentPath.getCurrentNodeIndex()));
    }

    public float getNodeReachProximity() {
        return this.nodeReachProximity;
    }

    public boolean isNearPathStartPos() {
        return this.nearPathStartPos;
    }
}


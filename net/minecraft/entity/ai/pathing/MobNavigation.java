/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation
extends EntityNavigation {
    private boolean avoidSunlight;

    public MobNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean isAtValidPosition() {
        return this.entity.isOnGround() || this.isInLiquid() || this.entity.hasVehicle();
    }

    @Override
    protected Vec3d getPos() {
        return new Vec3d(this.entity.getX(), this.getPathfindingY(), this.entity.getZ());
    }

    @Override
    public Path findPathTo(BlockPos target, int distance) {
        BlockPos blockPos;
        if (this.world.getBlockState(target).isAir()) {
            blockPos = target.down();
            while (blockPos.getY() > this.world.getBottomSectionLimit() && this.world.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.down();
            }
            if (blockPos.getY() > this.world.getBottomSectionLimit()) {
                return super.findPathTo(blockPos.up(), distance);
            }
            while (blockPos.getY() < this.world.getTopHeightLimit() && this.world.getBlockState(blockPos).isAir()) {
                blockPos = blockPos.up();
            }
            target = blockPos;
        }
        if (this.world.getBlockState(target).getMaterial().isSolid()) {
            blockPos = target.up();
            while (blockPos.getY() < this.world.getTopHeightLimit() && this.world.getBlockState(blockPos).getMaterial().isSolid()) {
                blockPos = blockPos.up();
            }
            return super.findPathTo(blockPos, distance);
        }
        return super.findPathTo(target, distance);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getBlockPos(), distance);
    }

    /**
     * The y-position to act as if the entity is at for pathfinding purposes
     */
    private int getPathfindingY() {
        if (!this.entity.isTouchingWater() || !this.canSwim()) {
            return MathHelper.floor(this.entity.getY() + 0.5);
        }
        int i = this.entity.getBlockY();
        BlockState blockState = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)i, this.entity.getZ()));
        int j = 0;
        while (blockState.isOf(Blocks.WATER)) {
            blockState = this.world.getBlockState(new BlockPos(this.entity.getX(), (double)(++i), this.entity.getZ()));
            if (++j <= 16) continue;
            return this.entity.getBlockY();
        }
        return i;
    }

    @Override
    protected void adjustPath() {
        super.adjustPath();
        if (this.avoidSunlight) {
            if (this.world.isSkyVisible(new BlockPos(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getLength(); ++i) {
                PathNode pathNode = this.currentPath.getNode(i);
                if (!this.world.isSkyVisible(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) continue;
                this.currentPath.setLength(i);
                return;
            }
        }
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor(origin.x);
        int j = MathHelper.floor(origin.z);
        double d = target.x - origin.x;
        double e = target.z - origin.z;
        double f = d * d + e * e;
        if (f < 1.0E-8) {
            return false;
        }
        double g = 1.0 / Math.sqrt(f);
        if (!this.allVisibleAreSafe(i, MathHelper.floor(origin.y), j, sizeX += 2, sizeY, sizeZ += 2, origin, d *= g, e *= g)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        double h = 1.0 / Math.abs(d);
        double k = 1.0 / Math.abs(e);
        double l = (double)i - origin.x;
        double m = (double)j - origin.z;
        if (d >= 0.0) {
            l += 1.0;
        }
        if (e >= 0.0) {
            m += 1.0;
        }
        l /= d;
        m /= e;
        int n = d < 0.0 ? -1 : 1;
        int o = e < 0.0 ? -1 : 1;
        int p = MathHelper.floor(target.x);
        int q = MathHelper.floor(target.z);
        int r = p - i;
        int s = q - j;
        while (r * n > 0 || s * o > 0) {
            if (l < m) {
                l += h;
                r = p - (i += n);
            } else {
                m += k;
                s = q - (j += o);
            }
            if (this.allVisibleAreSafe(i, MathHelper.floor(origin.y), j, sizeX, sizeY, sizeZ, origin, d, e)) continue;
            return false;
        }
        return true;
    }

    private boolean allVisibleAreSafe(int centerX, int centerY, int centerZ, int xSize, int ySize, int zSize, Vec3d entityPos, double lookVecX, double lookVecZ) {
        int i = centerX - xSize / 2;
        int j = centerZ - zSize / 2;
        if (!this.allVisibleArePassable(i, centerY, j, xSize, ySize, zSize, entityPos, lookVecX, lookVecZ)) {
            return false;
        }
        for (int k = i; k < i + xSize; ++k) {
            for (int l = j; l < j + zSize; ++l) {
                double d = (double)k + 0.5 - entityPos.x;
                double e = (double)l + 0.5 - entityPos.z;
                if (d * lookVecX + e * lookVecZ < 0.0) continue;
                PathNodeType pathNodeType = this.nodeMaker.getNodeType(this.world, k, centerY - 1, l, this.entity, xSize, ySize, zSize, true, true);
                if (!this.canWalkOnPath(pathNodeType)) {
                    return false;
                }
                pathNodeType = this.nodeMaker.getNodeType(this.world, k, centerY, l, this.entity, xSize, ySize, zSize, true, true);
                float f = this.entity.getPathfindingPenalty(pathNodeType);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (pathNodeType != PathNodeType.DAMAGE_FIRE && pathNodeType != PathNodeType.DANGER_FIRE && pathNodeType != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean canWalkOnPath(PathNodeType pathType) {
        if (pathType == PathNodeType.WATER) {
            return false;
        }
        if (pathType == PathNodeType.LAVA) {
            return false;
        }
        return pathType != PathNodeType.OPEN;
    }

    /**
     * Checks whether all blocks in the box which are visible (in front of) the mob can be pathed through
     */
    private boolean allVisibleArePassable(int x, int y, int z, int xSize, int ySize, int zSize, Vec3d entityPos, double lookVecX, double lookVecZ) {
        for (BlockPos blockPos : BlockPos.iterate(new BlockPos(x, y, z), new BlockPos(x + xSize - 1, y + ySize - 1, z + zSize - 1))) {
            double e;
            double d = (double)blockPos.getX() + 0.5 - entityPos.x;
            if (d * lookVecX + (e = (double)blockPos.getZ() + 0.5 - entityPos.z) * lookVecZ < 0.0 || this.world.getBlockState(blockPos).canPathfindThrough(this.world, blockPos, NavigationType.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setCanPathThroughDoors(boolean canPathThroughDoors) {
        this.nodeMaker.setCanOpenDoors(canPathThroughDoors);
    }

    public boolean canEnterOpenDoors() {
        return this.nodeMaker.canEnterOpenDoors();
    }

    public void setAvoidSunlight(boolean avoidSunlight) {
        this.avoidSunlight = avoidSunlight;
    }
}

